package ru.saidgadjiev.orm.next.core.stament_executor;

import ru.saidgadjiev.orm.next.core.criteria.impl.SelectStatement;
import ru.saidgadjiev.orm.next.core.db.DatabaseType;
import ru.saidgadjiev.orm.next.core.field.field_type.DBFieldType;
import ru.saidgadjiev.orm.next.core.field.field_type.ForeignFieldType;
import ru.saidgadjiev.orm.next.core.field.field_type.IDBFieldType;
import ru.saidgadjiev.orm.next.core.field.field_type.IndexFieldType;
import ru.saidgadjiev.orm.next.core.query.core.*;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectColumnsList;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.DisplayedOperand;
import ru.saidgadjiev.orm.next.core.query.core.common.UpdateValue;
import ru.saidgadjiev.orm.next.core.query.core.function.CountAll;
import ru.saidgadjiev.orm.next.core.query.core.literals.Param;
import ru.saidgadjiev.orm.next.core.query.visitor.DefaultVisitor;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.stament_executor.object.operation.ForeignCreator;
import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.ResultsMapper;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
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

/**
 * Класс для выполнения sql запросов
 *
 */
@SuppressWarnings("PMD")
public class StatementExecutorImpl implements IStatementExecutor {

    private TableInfo<?> tableInfo;

    private DatabaseType databaseType;

    private ResultsMapper<?> resultsMapper;

    private ForeignCreator<Object> foreignCreator;

    public StatementExecutorImpl(TableInfo<?> tableInfo,
                                 DatabaseType databaseType,
                                 ResultsMapper<?> resultsMapper,
                                 ForeignCreator<?> foreignCreator) {
        this.tableInfo = tableInfo;
        this.databaseType = databaseType;
        this.resultsMapper = resultsMapper;
        this.foreignCreator = (ForeignCreator<Object>) foreignCreator;
    }

    @Override
    public<T> int create(Connection connection, Collection<T> objects) throws SQLException {
        try {
            CreateQuery createQuery = new CreateQuery(tableInfo.getTableName());

            for (DBFieldType fieldType : tableInfo.toDBFieldTypes()) {
                if (fieldType.isId() && fieldType.isGenerated()) {
                    continue;
                }
                createQuery.add(new UpdateValue(
                        fieldType.getColumnName(),
                        new Param())
                );
            }
            for (ForeignFieldType fieldType : tableInfo.toForeignFieldTypes()) {
                createQuery.add(new UpdateValue(
                        fieldType.getColumnName(),
                        new Param())
                );
            }

            String query = getQuery(createQuery);
            try (IPreparedStatement statement = new PreparedQueryImpl(connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS), query)) {
                for (T object : objects) {
                    foreignCreator.execute(object);

                    for (Map.Entry<Integer, Object> entry : ArgumentUtils.eject(object, (TableInfo<T>) tableInfo).entrySet()) {
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
    public<T> int create(Connection connection, T object) throws SQLException {
        try {
            CreateQuery createQuery = new CreateQuery(tableInfo.getTableName());

            for (DBFieldType fieldType : tableInfo.toDBFieldTypes()) {
                if (fieldType.isId() && fieldType.isGenerated()) {
                    continue;
                }
                createQuery.add(new UpdateValue(
                        fieldType.getColumnName(),
                        new Param())
                );
            }
            for (ForeignFieldType fieldType : tableInfo.toForeignFieldTypes()) {
                createQuery.add(new UpdateValue(
                        fieldType.getColumnName(),
                        new Param())
                );
            }

            foreignCreator.execute(object);
            String query = getQuery(createQuery);

            try (IPreparedStatement statement = new PreparedQueryImpl(connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS), query)) {
                for (Map.Entry<Integer, Object> entry : ArgumentUtils.eject(object, (TableInfo<T>) tableInfo).entrySet()) {
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
    public<T> int update(Connection connection, T object) throws SQLException {
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
    public<T> int delete(Connection connection, T object) throws SQLException {
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
    public<ID> int deleteById(Connection connection, ID id) throws SQLException {
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
    public<T, ID> T queryForId(Connection connection, ID id) throws SQLException {
        IDBFieldType dbFieldType = tableInfo.getPrimaryKey().get();
        Select selectQuery = Select.buildQueryById(tableInfo.getTableName(), dbFieldType, id);
        String query = getQuery(selectQuery);

        try (IPreparedStatement preparedQuery = new PreparedQueryImpl(connection.prepareStatement(query), query)) {
            try (DatabaseResults databaseResults = preparedQuery.executeQuery()) {
                if (databaseResults.next()) {
                    return (T) resultsMapper.mapResults(databaseResults);
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
    public<T> List<T> queryForAll(Connection connection) throws SQLException {
        Select select = Select.buildQueryForAll(tableInfo.getTableName());
        List<T> resultObjectList = new ArrayList<>();
        String query = getQuery(select);

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
    public <R> GenericResults<R> query(ConnectionSource connectionSource, String query, Map<Integer, Object> args) throws SQLException {
        Connection connection = connectionSource.getConnection();
        IPreparedStatement statement = new PreparedQueryImpl(connection.prepareStatement(query), query);

        if (args != null) {
            for (Map.Entry<Integer, Object> entry : args.entrySet()) {
                statement.setObject(entry.getKey(), entry.getValue());
            }
        }

        return new GenericResultsImpl<>(connectionSource, connection, statement, (ResultsMapper<R>) resultsMapper);
    }

    @Override
    public long queryForLong(Connection connection, String query) throws SQLException {
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

        return new GenericResultsImpl<>(connectionSource, connection, preparedStatement, (ResultsMapper<R>) resultsMapper);
    }

    private String getQuery(QueryElement queryElement) {
        DefaultVisitor defaultVisitor = new DefaultVisitor(databaseType);

        queryElement.accept(defaultVisitor);

        return defaultVisitor.getQuery();
    }
}
