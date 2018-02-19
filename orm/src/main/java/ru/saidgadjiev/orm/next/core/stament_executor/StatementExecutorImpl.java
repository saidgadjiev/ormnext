package ru.saidgadjiev.orm.next.core.stament_executor;

import ru.saidgadjiev.orm.next.core.db.DatabaseType;
import ru.saidgadjiev.orm.next.core.field.field_type.IDBFieldType;
import ru.saidgadjiev.orm.next.core.field.field_type.IndexFieldType;
import ru.saidgadjiev.orm.next.core.query.core.*;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectColumnsList;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.DisplayedOperand;
import ru.saidgadjiev.orm.next.core.query.core.function.CountAll;
import ru.saidgadjiev.orm.next.core.query.visitor.DefaultVisitor;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.stament_executor.object.ArgumentEjector;
import ru.saidgadjiev.orm.next.core.stament_executor.object.CreateQueryBuilder;
import ru.saidgadjiev.orm.next.core.stament_executor.object.operation.ForeignCreator;
import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.ResultsMapper;
import ru.saidgadjiev.orm.next.core.table.TableInfo;
import ru.saidgadjiev.orm.next.core.table.TableInfoManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Класс для выполнения sql запросов
 */
@SuppressWarnings("PMD")
public class StatementExecutorImpl implements IStatementExecutor {

    private Function<TableInfo<?>, CreateQueryBuilder<?>> objectCreatorFactory;

    private Function<TableInfo<?>, ResultsMapper<?>> resultsMapper;

    private DatabaseType databaseType;

    private ForeignCreator foreignCreator;

    public StatementExecutorImpl(Function<TableInfo<?>, CreateQueryBuilder<?>> objectCreatorFactory,
                                 DatabaseType databaseType,
                                 Function<TableInfo<?>, ResultsMapper<?>> resultsMapper,
                                 ForeignCreator foreignCreator) {
        this.objectCreatorFactory = objectCreatorFactory;
        this.databaseType = databaseType;
        this.resultsMapper = resultsMapper;
        this.foreignCreator = foreignCreator;
    }

    @Override
    public <T> int create(Connection connection, Collection<T> objects, Class<T> tClass) throws SQLException {
        try {
            if (objects.isEmpty()) {
                return 0;
            }
            TableInfo<T> tableInfo = TableInfoManager.buildOrGet(tClass);
            CreateQueryBuilder<T> createQueryBuilder = (CreateQueryBuilder<T>) objectCreatorFactory.apply(tableInfo);

            CreateQuery createQuery = createQueryBuilder
                    .newObject()
                    .createBase()
                    .createForeign()
                    .query();

            String query = getQuery(createQuery);
            try (IPreparedStatement statement = new PreparedQueryImpl(connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS), query)) {
                for (T object : objects) {
                    foreignCreator.execute(tableInfo, object);

                    for (Map.Entry<Integer, Object> entry : ArgumentEjector.eject(object, tableInfo).entrySet()) {
                        statement.setObject(entry.getKey(), entry.getValue());
                    }
                    statement.addBatch();
                }
                int[] result = statement.executeBatch();
                int count = 0;

                for (int c : result) {
                    count += c;
                }

                return count;
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида INSERT INTO ...(colname1, colname2, ...) VALUES(colvalue1, colvalue2, ...)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> int create(Connection connection, T object) throws SQLException {
        try {
            TableInfo<T> tableInfo = TableInfoManager.buildOrGet((Class<T>) object.getClass());
            CreateQueryBuilder<T> createQueryBuilder = (CreateQueryBuilder<T>) objectCreatorFactory.apply(tableInfo);

            CreateQuery createQuery = createQueryBuilder
                    .newObject()
                    .createBase()
                    .createForeign()
                    .query();
            foreignCreator.execute(tableInfo, object);
            String query = getQuery(createQuery);

            try (IPreparedStatement statement = new PreparedQueryImpl(connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS), query)) {
                for (Map.Entry<Integer, Object> entry : ArgumentEjector.eject(object, tableInfo).entrySet()) {
                    statement.setObject(entry.getKey(), entry.getValue());
                }
                Integer result = statement.executeUpdate();

                if (tableInfo.getPrimaryKey().isPresent()) {
                    IDBFieldType idField = tableInfo.getPrimaryKey().get();

                    ResultSet resultSet = statement.getGeneratedKeys();
                    try (GeneratedKeys generatedKeys = new GeneratedKeys(resultSet, resultSet.getMetaData())) {
                        if (generatedKeys.next()) {
                            try {
                                idField.assignId(object, generatedKeys.getGeneratedKey());
                            } catch (IllegalAccessException ex) {
                                throw new SQLException(ex);
                            }
                        }
                    }
                }

                return result;
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public <T> boolean createTable(Connection connection, Class<T> tClass, boolean ifNotExists) throws SQLException {
        try (IStatement statement = new StatementImpl(connection.createStatement())) {
            CreateTableQuery createTableQuery = CreateTableQuery.buildQuery(
                    TableInfoManager.buildOrGet(tClass),
                    ifNotExists
            );
            statement.executeUpdate(getQuery(createTableQuery));

            return true;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public <T> boolean dropTable(Connection connection, Class<T> tClass, boolean ifExists) throws SQLException {
        try (IStatement statement = new StatementImpl(connection.createStatement())) {
            DropTableQuery dropTableQuery = DropTableQuery.buildQuery(TableInfoManager.buildOrGet(tClass).getTableName(), ifExists);

            statement.executeUpdate(getQuery(dropTableQuery));

            return true;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Обновляет объект в базе
     * Выполняет запрос вида UPDATE ... SET colname1 = colvalue1 SET colname2 = colvalue2 WHERE = object_builder.id
     */
    @Override
    public <T> int update(Connection connection, T object) throws SQLException {
        try {
            TableInfo<T> tableInfo = TableInfoManager.buildOrGet(object.getClass());
            IDBFieldType idFieldType = tableInfo.getPrimaryKey().get();
            UpdateQuery updateQuery = UpdateQuery.buildQuery(
                    tableInfo.getTableName(),
                    tableInfo.toDBFieldTypes(),
                    idFieldType.getColumnName(),
                    object
            );
            String query = getQuery(updateQuery);
            AtomicInteger index = new AtomicInteger();

            try (IPreparedStatement preparedQuery = new PreparedQueryImpl(connection.prepareStatement(query), query)) {
                for (IDBFieldType idbFieldType : tableInfo.toDBFieldTypes()) {
                    Object value = idbFieldType.access(object);

                    preparedQuery.setObject(index.incrementAndGet(), value);
                }
                preparedQuery.setObject(index.incrementAndGet(), idFieldType.access(object));

                return preparedQuery.executeUpdate();
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида DELETE FROM ... WHERE = object_builder.id
     */
    @Override
    public <T> int delete(Connection connection, T object) throws SQLException {
        try {
            TableInfo<T> tableInfo = TableInfoManager.buildOrGet(object.getClass());
            IDBFieldType dbFieldType = tableInfo.getPrimaryKey().get();
            Object id = dbFieldType.access(object);
            DeleteQuery deleteQuery = DeleteQuery.buildQuery(tableInfo.getTableName(), dbFieldType.getColumnName());
            String query = getQuery(deleteQuery);

            try (IPreparedStatement preparedQuery = new PreparedQueryImpl(connection.prepareStatement(query), query)) {
                preparedQuery.setObject(1, id);

                return preparedQuery.executeUpdate();
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида DELETE FROM ... WHERE = object_builder.id
     */
    @Override
    public <T, ID> int deleteById(Connection connection, Class<T> tClass, ID id) throws SQLException {
        try {
            TableInfo<T> tableInfo = TableInfoManager.buildOrGet(tClass);
            IDBFieldType dbFieldType = tableInfo.getPrimaryKey().get();
            DeleteQuery deleteQuery = DeleteQuery.buildQuery(tableInfo.getTableName(), dbFieldType.getColumnName());
            String query = getQuery(deleteQuery);

            try (IPreparedStatement preparedQuery = new PreparedQueryImpl(connection.prepareStatement(query), query)) {
                preparedQuery.setObject(1, id);

                return preparedQuery.executeUpdate();
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Возвращает объект по id
     * Выполняет запрос вида SELECT * FROM ... WHERE = id
     */
    @Override
    public <T, ID> T queryForId(Connection connection, Class<T> tClass, ID id) throws SQLException {
        try {
            TableInfo<T> tableInfo = TableInfoManager.buildOrGet(tClass);
            IDBFieldType dbFieldType = tableInfo.getPrimaryKey().get();
            Select selectQuery = Select.buildQueryById(tableInfo.getTableName(), dbFieldType, id);
            String query = getQuery(selectQuery);

            try (IPreparedStatement preparedQuery = new PreparedQueryImpl(connection.prepareStatement(query), query)) {
                try (DatabaseResults databaseResults = preparedQuery.executeQuery()) {
                    if (databaseResults.next()) {
                        return (T) resultsMapper.apply(tableInfo).mapResults(databaseResults);
                    }
                }
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }

        return null;
    }

    /**
     * Возвращает все объекты из таблицы
     * Выполняет запрос вида SELECT * FROM ...
     */
    @Override
    public <T> List<T> queryForAll(Connection connection, Class<T> tClass) throws SQLException {
        try {
            TableInfo<T> tableInfo = TableInfoManager.buildOrGet(tClass);
            Select select = Select.buildQueryForAll(tableInfo.getTableName());
            List<T> resultObjectList = new ArrayList<>();
            String query = getQuery(select);

            try (IStatement statement = new StatementImpl(connection.createStatement())) {
                try (DatabaseResults databaseResults = statement.executeQuery(query)) {
                    while (databaseResults.next()) {
                        resultObjectList.add((T) resultsMapper.apply(tableInfo).mapResults(databaseResults));
                    }
                }
            }

            return resultObjectList;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }


    @Override
    public <T> void createIndexes(Connection connection, Class<T> tClass) throws SQLException {
        try {
            TableInfo<T> tableInfo = TableInfoManager.buildOrGet(tClass);
            List<IndexFieldType> indexFieldTypes = tableInfo.getIndexFieldTypes();

            for (IndexFieldType indexFieldType : indexFieldTypes) {
                CreateIndexQuery createIndexQuery = new CreateIndexQuery(indexFieldType);

                try (IStatement statement = new StatementImpl(connection.createStatement())) {
                    statement.executeUpdate(getQuery(createIndexQuery));
                }
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public <T> void dropIndexes(Connection connection, Class<T> tClass) throws SQLException {
        try {
            TableInfo<T> tableInfo = TableInfoManager.buildOrGet(tClass);
            List<IndexFieldType> indexFieldTypes = tableInfo.getIndexFieldTypes();

            for (IndexFieldType indexFieldType : indexFieldTypes) {
                DropIndexQuery dropIndexQuery = DropIndexQuery.build(indexFieldType.getName());

                try (IStatement statement = new StatementImpl(connection.createStatement())) {
                    statement.executeUpdate(getQuery(dropIndexQuery));
                }
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public <R> GenericResults<R> query(Connection connection, String query, ResultsMapper<R> resultsMapper) throws SQLException {
        try (IStatement statement = new StatementImpl(connection.createStatement())) {
            try (DatabaseResults databaseResults = statement.executeQuery(query)) {
                return new GenericResults<R>() {
                    @Override
                    public List<R> getResults() throws SQLException {
                        try {
                            List<R> objects = new ArrayList<>();

                            while (databaseResults.next()) {
                                objects.add(resultsMapper.mapResults(databaseResults));
                            }

                            return objects;
                        } catch (Exception e) {
                            throw new SQLException(e);
                        }
                    }

                    @Override
                    public R getFirstResult() throws SQLException {
                        return null;
                    }
                };
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public long queryForLong(String query, Connection connection) throws SQLException {
        try (IStatement statement = new StatementImpl(connection.createStatement())) {
            try (DatabaseResults databaseResults = statement.executeQuery(query)) {
                if (databaseResults.next()) {
                    return databaseResults.getLong(1);
                }
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }

        throw new SQLException("No result found in queryForLong: " + query);
    }

    @Override
    public <T> long countOff(Connection connection, Class<T> tClass) throws SQLException {
        try {
            TableInfo<T> tableInfo = TableInfoManager.buildOrGet(tClass);
            Select select = Select.buildQueryForAll(tableInfo.getTableName());
            SelectColumnsList selectColumnsList = new SelectColumnsList();

            selectColumnsList.addColumn(new DisplayedOperand(new CountAll()));
            select.setSelectColumnsStrategy(selectColumnsList);

            return queryForLong(getQuery(select), connection);
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    private String getQuery(QueryElement queryElement) {
        DefaultVisitor defaultVisitor = new DefaultVisitor(databaseType);

        queryElement.accept(defaultVisitor);

        return defaultVisitor.getQuery();
    }
}
