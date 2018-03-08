package ru.saidgadjiev.orm.next.core.stament_executor;

import ru.saidgadjiev.orm.next.core.criteria.impl.SelectStatement;
import ru.saidgadjiev.orm.next.core.db.DatabaseType;
import ru.saidgadjiev.orm.next.core.field.field_type.IDBFieldType;
import ru.saidgadjiev.orm.next.core.field.field_type.IndexFieldType;
import ru.saidgadjiev.orm.next.core.query.core.*;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectColumnsList;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.DisplayedOperand;
import ru.saidgadjiev.orm.next.core.query.core.function.CountAll;
import ru.saidgadjiev.orm.next.core.query.visitor.DefaultVisitor;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.stament_executor.object.CreateQueryBuilder;
import ru.saidgadjiev.orm.next.core.stament_executor.object.operation.ForeignCreator;
import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.ResultsMapper;
import ru.saidgadjiev.orm.next.core.table.TableInfo;
import ru.saidgadjiev.orm.next.core.utils.ArgumentUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Класс для выполнения sql запросов
 *
 * @param <T>  тип объекта
 * @param <ID> id объекта
 */
@SuppressWarnings("PMD")
public class StatementExecutorImpl<T, ID> implements IStatementExecutor<T, ID> {

    private TableInfo<T> tableInfo;

    private Supplier<CreateQueryBuilder<T>> objectCreatorFactory;

    private DatabaseType databaseType;

    private ResultsMapper<T> resultsMapper;

    private ForeignCreator<T> foreignCreator;

    public StatementExecutorImpl(TableInfo<T> tableInfo,
                                 Supplier<CreateQueryBuilder<T>> objectCreatorFactory,
                                 DatabaseType databaseType,
                                 ResultsMapper<T> resultsMapper,
                                 ForeignCreator<T> foreignCreator) {
        this.tableInfo = tableInfo;
        this.objectCreatorFactory = objectCreatorFactory;
        this.databaseType = databaseType;
        this.resultsMapper = resultsMapper;
        this.foreignCreator = foreignCreator;
    }

    @Override
    public int create(Connection connection, Collection<T> objects) throws SQLException {
        try {
            CreateQueryBuilder<T> createQueryBuilder = objectCreatorFactory.get();

            CreateQuery createQuery = createQueryBuilder
                    .newObject()
                    .createBase()
                    .createForeign()
                    .query();

            String query = getQuery(createQuery);
            try (IPreparedStatement statement = new PreparedQueryImpl(connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS), query)) {
                for (T object : objects) {
                    foreignCreator.execute(object);

                    for (Map.Entry<Integer, Object> entry : ArgumentUtils.eject(object, tableInfo).entrySet()) {
                        statement.setObject(entry.getKey(), entry.getValue());
                    }
                    statement.addBatch();
                }
                int[] result = statement.executeBatch();
                int count = 0;

                for (int c: result) {
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
    public int create(Connection connection, T object) throws SQLException {
        try {
            CreateQueryBuilder<T> createQueryBuilder = objectCreatorFactory.get();

            CreateQuery createQuery = createQueryBuilder
                    .newObject()
                    .createBase()
                    .createForeign()
                    .query();
            foreignCreator.execute(object);
            String query = getQuery(createQuery);

            try (IPreparedStatement statement = new PreparedQueryImpl(connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS), query)) {
                for (Map.Entry<Integer, Object> entry : ArgumentUtils.eject(object, tableInfo).entrySet()) {
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
    public boolean createTable(Connection connection, boolean ifNotExists) throws SQLException {
        CreateTableQuery createTableQuery = CreateTableQuery.buildQuery(
                tableInfo,
                ifNotExists
        );

        try (IStatement statement = new StatementImpl(connection.createStatement())) {
            statement.executeUpdate(getQuery(createTableQuery));

            return true;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public boolean dropTable(Connection connection, boolean ifExists) throws SQLException {
        DropTableQuery dropTableQuery = DropTableQuery.buildQuery(tableInfo.getTableName(), ifExists);

        try (IStatement statement = new StatementImpl(connection.createStatement())) {
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
    public int update(Connection connection, T object) throws SQLException {
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
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида DELETE FROM ... WHERE = object_builder.id
     */
    @Override
    public int delete(Connection connection, T object) throws SQLException {
        try {
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
    public int deleteById(Connection connection, ID id) throws SQLException {
        IDBFieldType dbFieldType = tableInfo.getPrimaryKey().get();
        DeleteQuery deleteQuery = DeleteQuery.buildQuery(tableInfo.getTableName(), dbFieldType.getColumnName());
        String query = getQuery(deleteQuery);

        try (IPreparedStatement preparedQuery = new PreparedQueryImpl(connection.prepareStatement(query), query)) {
            preparedQuery.setObject(1, id);

            return preparedQuery.executeUpdate();
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Возвращает объект по id
     * Выполняет запрос вида SELECT * FROM ... WHERE = id
     */
    @Override
    public T queryForId(Connection connection, ID id) throws SQLException {
        IDBFieldType dbFieldType = tableInfo.getPrimaryKey().get();
        Select selectQuery = Select.buildQueryById(tableInfo.getTableName(), dbFieldType, id);
        String query = getQuery(selectQuery);

        try (IPreparedStatement preparedQuery = new PreparedQueryImpl(connection.prepareStatement(query), query)) {
            try (DatabaseResults databaseResults = preparedQuery.executeQuery()) {
                if (databaseResults.next()) {
                    return resultsMapper.mapResults(databaseResults);
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
    public List<T> queryForAll(Connection connection) throws SQLException {
        Select select = Select.buildQueryForAll(tableInfo.getTableName());
        List<T> resultObjectList = new ArrayList<>();
        String query = getQuery(select);

        try (IStatement statement = new StatementImpl(connection.createStatement())) {
            try (DatabaseResults databaseResults = statement.executeQuery(query)) {
                while (databaseResults.next()) {
                    resultObjectList.add(resultsMapper.mapResults(databaseResults));
                }
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }

        return resultObjectList;
    }


    @Override
    public void createIndexes(Connection connection) throws SQLException {
        List<IndexFieldType> indexFieldTypes = tableInfo.getIndexFieldTypes();

        for (IndexFieldType indexFieldType : indexFieldTypes) {
            CreateIndexQuery createIndexQuery = new CreateIndexQuery(indexFieldType);

            try (IStatement statement = new StatementImpl(connection.createStatement())) {
                statement.executeUpdate(getQuery(createIndexQuery));
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    @Override
    public void dropIndexes(Connection connection) throws SQLException {
        List<IndexFieldType> indexFieldTypes = tableInfo.getIndexFieldTypes();

        for (IndexFieldType indexFieldType : indexFieldTypes) {
            DropIndexQuery dropIndexQuery = DropIndexQuery.build(indexFieldType.getName());

            try (IStatement statement = new StatementImpl(connection.createStatement())) {
                statement.executeUpdate(getQuery(dropIndexQuery));
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
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
    public long query(String query, Connection connection) throws SQLException {
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
    public long countOff(Connection connection) throws SQLException {
        Select select = Select.buildQueryForAll(tableInfo.getTableName());
        SelectColumnsList selectColumnsList = new SelectColumnsList();

        selectColumnsList.addColumn(new DisplayedOperand(new CountAll()));
        select.setSelectColumnsStrategy(selectColumnsList);

        return query(getQuery(select), connection);
    }

    @Override
    public List<T> query(Connection connection, SelectStatement<T> statement, ResultsMapper<T> resultsMapper) throws SQLException {
        List<T> resultObjectList = new ArrayList<>();
        String query = getQuery(statement);

        try (IPreparedStatement preparedStatement = new PreparedQueryImpl(connection.prepareStatement(query), query)) {
            for (Map.Entry<Integer, Object> entry: statement.getArgs().entrySet()) {
                preparedStatement.setObject(entry.getKey(), entry.getValue());
            }
            try (DatabaseResults databaseResults = preparedStatement.executeQuery()) {
                while (databaseResults.next()) {
                    if (resultsMapper != null) {
                        resultObjectList.add(resultsMapper.mapResults(databaseResults));
                    } else {
                        resultObjectList.add(this.resultsMapper.mapResults(databaseResults));
                    }
                }
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }

        return resultObjectList;
    }

    private String getQuery(QueryElement queryElement) {
        DefaultVisitor defaultVisitor = new DefaultVisitor(databaseType);

        queryElement.accept(defaultVisitor);

        return defaultVisitor.getQuery();
    }
}
