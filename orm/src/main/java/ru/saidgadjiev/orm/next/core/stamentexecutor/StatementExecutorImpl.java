package ru.saidgadjiev.orm.next.core.stamentexecutor;

import ru.saidgadjiev.orm.next.core.criteria.impl.SelectStatement;
import ru.saidgadjiev.orm.next.core.dao.Session;
import ru.saidgadjiev.orm.next.core.dao.metamodel.MetaModel;
import ru.saidgadjiev.orm.next.core.db.DatabaseType;
import ru.saidgadjiev.orm.next.core.field.fieldtype.DatabaseColumnType;
import ru.saidgadjiev.orm.next.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.orm.next.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.orm.next.core.field.fieldtype.IndexFieldType;
import ru.saidgadjiev.orm.next.core.query.core.*;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectColumnsList;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.DisplayedOperand;
import ru.saidgadjiev.orm.next.core.query.core.common.UpdateValue;
import ru.saidgadjiev.orm.next.core.query.core.function.CountAll;
import ru.saidgadjiev.orm.next.core.query.core.literals.Param;
import ru.saidgadjiev.orm.next.core.query.visitor.DefaultVisitor;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.query.visitor.SelectColumnAliasesVisitor;
import ru.saidgadjiev.orm.next.core.stamentexecutor.object.operation.ForeignCreator;
import ru.saidgadjiev.orm.next.core.stamentexecutor.resultmapper.ResultsMapper;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.table.DatabaseEntityMetadata;
import ru.saidgadjiev.orm.next.core.table.TableInfoManager;
import ru.saidgadjiev.orm.next.core.table.persister.DatabaseEntityPersister;
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

/**
 * Класс для выполнения sql запросов
 */
@SuppressWarnings("PMD")
public class StatementExecutorImpl implements IStatementExecutor {

    private Session session;

    private MetaModel metaModel;

    private DatabaseType databaseType;

    private ForeignCreator foreignCreator;

    public StatementExecutorImpl(Session session,
                                 MetaModel metaModel,
                                 DatabaseType databaseType,
                                 ForeignCreator foreignCreator) {
        this.session = session;
        this.metaModel = metaModel;
        this.databaseType = databaseType;
        this.foreignCreator = foreignCreator;
    }

    @Override
    public <T> int create(Connection connection, Collection<T> objects) throws SQLException {
        try {
            if (objects.isEmpty()) {
                return 0;
            }
            Class<T> tClass = (Class<T>) objects.iterator().next().getClass();
            DatabaseEntityMetadata<T> databaseEntityMetadata = TableInfoManager.buildOrGet(tClass);
            CreateQuery createQuery = new CreateQuery(databaseEntityMetadata.getTableName());

            for (DatabaseColumnType fieldType : databaseEntityMetadata.toDBFieldTypes()) {
                if (fieldType.isId() && fieldType.isGenerated()) {
                    continue;
                }
                createQuery.add(new UpdateValue(
                        fieldType.getColumnName(),
                        new Param())
                );
            }
            for (ForeignColumnType fieldType : databaseEntityMetadata.toForeignFieldTypes()) {
                createQuery.add(new UpdateValue(
                        fieldType.getColumnName(),
                        new Param())
                );
            }

            String query = getQuery(createQuery);
            try (IPreparedStatement statement = new PreparedQueryImpl(connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS), query)) {
                for (T object : objects) {
                    foreignCreator.execute(object);

                    for (Map.Entry<Integer, Object> entry : ArgumentUtils.eject(object, databaseEntityMetadata).entrySet()) {
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
            DatabaseEntityMetadata<T> databaseEntityMetadata = TableInfoManager.buildOrGet((Class<T>) object.getClass());
            CreateQuery createQuery = new CreateQuery(databaseEntityMetadata.getTableName());

            for (DatabaseColumnType fieldType : databaseEntityMetadata.toDBFieldTypes()) {
                if (fieldType.isId() && fieldType.isGenerated()) {
                    continue;
                }
                createQuery.add(new UpdateValue(
                        fieldType.getColumnName(),
                        new Param())
                );
            }
            for (ForeignColumnType fieldType : databaseEntityMetadata.toForeignFieldTypes()) {
                createQuery.add(new UpdateValue(
                        fieldType.getColumnName(),
                        new Param())
                );
            }

            foreignCreator.execute(object);
            String query = getQuery(createQuery);

            try (IPreparedStatement statement = new PreparedQueryImpl(connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS), query)) {
                for (Map.Entry<Integer, Object> entry : ArgumentUtils.eject(object, databaseEntityMetadata).entrySet()) {
                    statement.setObject(entry.getKey(), entry.getValue());
                }
                Integer result = statement.executeUpdate();

                IDatabaseColumnType idField = databaseEntityMetadata.getPrimaryKey();

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

                return result;
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public <T> boolean createTable(Connection connection, Class<T> tClass, boolean ifNotExists) throws SQLException {
        DatabaseEntityMetadata<T> databaseEntityMetadata = TableInfoManager.buildOrGet(tClass);
        CreateTableQuery createTableQuery = CreateTableQuery.buildQuery(
                databaseEntityMetadata,
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
    public <T> boolean dropTable(Connection connection, Class<T> tClass, boolean ifExists) throws SQLException {
        DatabaseEntityMetadata<T> databaseEntityMetadata = TableInfoManager.buildOrGet(tClass);
        DropTableQuery dropTableQuery = DropTableQuery.buildQuery(databaseEntityMetadata.getTableName(), ifExists);

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
    public <T> int update(Connection connection, T object) throws SQLException {
        DatabaseEntityMetadata<T> databaseEntityMetadata = TableInfoManager.buildOrGet((Class<T>) object.getClass());
        IDatabaseColumnType idFieldType = databaseEntityMetadata.getPrimaryKey();
        UpdateQuery updateQuery = UpdateQuery.buildQuery(
                databaseEntityMetadata.getTableName(),
                databaseEntityMetadata.toDBFieldTypes(),
                idFieldType.getColumnName(),
                object
        );
        String query = getQuery(updateQuery);
        AtomicInteger index = new AtomicInteger();

        try (IPreparedStatement preparedQuery = new PreparedQueryImpl(connection.prepareStatement(query), query)) {
            for (IDatabaseColumnType IDatabaseColumnType : databaseEntityMetadata.toDBFieldTypes()) {
                Object value = IDatabaseColumnType.access(object);

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
    public <T> int delete(Connection connection, T object) throws SQLException {
        try {
            DatabaseEntityMetadata<T> databaseEntityMetadata = TableInfoManager.buildOrGet((Class<T>) object.getClass());
            IDatabaseColumnType dbFieldType = databaseEntityMetadata.getPrimaryKey();
            Object id = dbFieldType.access(object);
            DeleteQuery deleteQuery = DeleteQuery.buildQuery(databaseEntityMetadata.getTableName(), dbFieldType.getColumnName());
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
        DatabaseEntityMetadata<T> databaseEntityMetadata = TableInfoManager.buildOrGet(tClass);
        IDatabaseColumnType dbFieldType = databaseEntityMetadata.getPrimaryKey();
        DeleteQuery deleteQuery = DeleteQuery.buildQuery(databaseEntityMetadata.getTableName(), dbFieldType.getColumnName());
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
    public <T, ID> T queryForId(Connection connection, Class<T> tClass, ID id) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        Select selectQuery = new Select();

        selectQuery.setFrom(entityPersister.getFromExpression());
        selectQuery.setSelectColumnsStrategy(entityPersister.getSelectColumnsList());
        selectQuery.appendByIdClause(entityPersister.getMetadata(), entityPersister.getAliases().getTableAlias(), id);
        String query = getQuery(selectQuery);

        try (IPreparedStatement preparedQuery = new PreparedQueryImpl(connection.prepareStatement(query), query)) {
            try (DatabaseResults databaseResults = preparedQuery.executeQuery()) {
                List<Object> results = entityPersister.load(session, databaseResults);

                if (results.isEmpty()) {
                    return null;
                }

                return (T) results.iterator().next();
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Возвращает все объекты из таблицы
     * Выполняет запрос вида SELECT * FROM ...
     */
    @Override
    public <T> List<T> queryForAll(Connection connection, Class<T> tClass) throws SQLException {
        DatabaseEntityMetadata<T> databaseEntityMetadata = TableInfoManager.buildOrGet(tClass);
        Select select = Select.buildQueryForAll(databaseEntityMetadata.getTableName());
        List<T> resultObjectList = new ArrayList<>();
        String query = getQuery(select);
        ResultsMapper<?> resultsMapper = null;

        try (IStatement statement = new StatementImpl(connection.createStatement())) {
            try (DatabaseResults databaseResults = statement.executeQuery(query)) {
                while (databaseResults.next()) {
                    resultObjectList.add((T) resultsMapper.mapResults(databaseResults));
                }
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }

        return resultObjectList;
    }


    @Override
    public <T> void createIndexes(Connection connection, Class<T> tClass) throws SQLException {
        DatabaseEntityMetadata<T> databaseEntityMetadata = TableInfoManager.buildOrGet(tClass);
        List<IndexFieldType> indexFieldTypes = databaseEntityMetadata.getIndexFieldTypes();

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
    public <T> void dropIndexes(Connection connection, Class<T> tClass) throws SQLException {
        DatabaseEntityMetadata<T> databaseEntityMetadata = TableInfoManager.buildOrGet(tClass);
        List<IndexFieldType> indexFieldTypes = databaseEntityMetadata.getIndexFieldTypes();

        for (IndexFieldType indexFieldType : indexFieldTypes) {
            DropIndexQuery dropIndexQuery = DropIndexQuery.build(indexFieldType.getName());

            try (IStatement statement = new StatementImpl(connection.createStatement())) {
                statement.executeUpdate(getQuery(dropIndexQuery));
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    private long queryForLong(Connection connection, String query) throws SQLException {
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
        DatabaseEntityMetadata<T> databaseEntityMetadata = TableInfoManager.buildOrGet(tClass);
        Select select = Select.buildQueryForAll(databaseEntityMetadata.getTableName());
        SelectColumnsList selectColumnsList = new SelectColumnsList();

        selectColumnsList.addColumn(new DisplayedOperand(new CountAll()));
        select.setSelectColumnsStrategy(selectColumnsList);

        return queryForLong(connection, getQuery(select));
    }

    @Override
    public <R> GenericResults<R> query(ConnectionSource connectionSource, SelectStatement<R> statement) throws SQLException {
        String query = getQuery(statement);
        Connection connection = connectionSource.getConnection();
        IPreparedStatement preparedStatement = new PreparedQueryImpl(connection.prepareStatement(query), query);
        for (Map.Entry<Integer, Object> entry : statement.getArgs().entrySet()) {
            preparedStatement.setObject(entry.getKey(), entry.getValue());
        }

        return new GenericResultsImpl<>(
                connectionSource,
                connection,
                preparedStatement,
                null);
    }

    private String getQuery(QueryElement queryElement) {
        DefaultVisitor defaultVisitor = new DefaultVisitor(databaseType);

        queryElement.accept(defaultVisitor);

        return defaultVisitor.getQuery();
    }

    private Map<String, String> getAliases(QueryElement queryElement) {
        SelectColumnAliasesVisitor visitor = new SelectColumnAliasesVisitor();

        queryElement.accept(visitor);

        return visitor.getColumnAliasMap();
    }
}
