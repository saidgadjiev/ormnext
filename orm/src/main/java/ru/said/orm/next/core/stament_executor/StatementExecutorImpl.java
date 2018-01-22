package ru.said.orm.next.core.stament_executor;

import ru.said.orm.next.core.field.field_type.DBFieldType;
import ru.said.orm.next.core.field.field_type.IndexFieldType;
import ru.said.orm.next.core.query.core.*;
import ru.said.orm.next.core.query.visitor.DefaultVisitor;
import ru.said.orm.next.core.query.visitor.QueryElement;
import ru.said.orm.next.core.stament_executor.object.DataBaseObject;
import ru.said.orm.next.core.table.TableInfo;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для выполнения sql запросов
 *
 * @param <T>  тип объекта
 * @param <ID> id объекта
 */
@SuppressWarnings("PMD")
public class StatementExecutorImpl<T, ID> implements IStatementExecutor<T, ID> {

    private DataBaseObject<T> dataBaseObject;

    StatementExecutorImpl(DataBaseObject<T> dataBaseObject) {
        this.dataBaseObject = dataBaseObject;
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида INSERT INTO ...(colname1, colname2, ...) VALUES(colvalue1, colvalue2, ...)
     */
    @Override
    @SuppressWarnings("unchecked")
    public int create(Connection connection, T object) throws SQLException {
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();

        try {
            CreateQuery query = dataBaseObject.getObjectCreator().newObject(object)
                    .createBase(object)
                    .createForeign(object)
                    .query();

            try (PreparedQueryImpl preparedQuery = new PreparedQueryImpl(connection.prepareStatement(getQuery(query)))) {
                Integer result = preparedQuery.executeUpdate();

                if (tableInfo.getPrimaryKeys().isPresent()) {
                    DBFieldType idField = tableInfo.getPrimaryKeys().get();

                    ResultSet resultSet = preparedQuery.getGeneratedKeys();
                    try (GeneratedKeys generatedKeys = new GeneratedKeys(resultSet, resultSet.getMetaData())) {
                        try {
                            idField.assign(object, generatedKeys.getGeneratedKey());
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
    public boolean createTable(Connection connection, boolean ifNotExists) throws SQLException {
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();
        CreateTableQuery createTableQuery = CreateTableQuery.buildQuery(
                tableInfo,
                ifNotExists
        );

        try (PreparedQueryImpl preparedQuery = new PreparedQueryImpl(connection.prepareStatement(getQuery(createTableQuery)))) {
            preparedQuery.executeUpdate();

            return true;
        }
    }

    @Override
    public boolean dropTable(Connection connection, boolean ifExists) throws SQLException {
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();
        DropTableQuery dropTableQuery = DropTableQuery.buildQuery(tableInfo.getTableName(), ifExists);

        try (PreparedQueryImpl preparedQuery = new PreparedQueryImpl(connection.prepareStatement(null))) {
            preparedQuery.executeUpdate();

            return true;
        }
    }

    /**
     * Обновляет объект в базе
     * Выполняет запрос вида UPDATE ... SET colname1 = colvalue1 SET colname2 = colvalue2 WHERE = object_builder.id
     */
    @Override
    public int update(Connection connection, T object) throws SQLException {
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();
        DBFieldType idFieldType = tableInfo.getPrimaryKeys().get();
        UpdateQuery query = UpdateQuery.buildQuery(
                tableInfo.getTableName(),
                tableInfo.toDBFieldTypes(),
                idFieldType.getColumnName(),
                object
        );

        try (PreparedQueryImpl preparedQuery = new PreparedQueryImpl(connection.prepareStatement(getQuery(query)))) {
            preparedQuery.setObject(0, idFieldType.access(object));

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
            TableInfo<T> tableInfo = dataBaseObject.getTableInfo();
            DBFieldType dbFieldType = tableInfo.getPrimaryKeys().get();
            Object id = dbFieldType.access(object);
            DeleteQuery query = DeleteQuery.buildQuery(tableInfo.getTableName(), dbFieldType.getColumnName());

            try (PreparedQueryImpl preparedQuery = new PreparedQueryImpl(connection.prepareStatement(getQuery(query)))) {
                preparedQuery.setObject(0, id);

                return preparedQuery.executeUpdate();
            }
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида DELETE FROM ... WHERE = object_builder.id
     */
    @Override
    public int deleteById(Connection connection, ID id) throws SQLException {
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();
        DBFieldType dbFieldType = tableInfo.getPrimaryKeys().get();
        DeleteQuery query = DeleteQuery.buildQuery(tableInfo.getTableName(), dbFieldType.getColumnName());

        try (PreparedQueryImpl preparedQuery = new PreparedQueryImpl(connection.prepareStatement(getQuery(query)))) {
            preparedQuery.setObject(0, id);

            return preparedQuery.executeUpdate();
        }
    }

    /**
     * Возвращает объект по id
     * Выполняет запрос вида SELECT * FROM ... WHERE = id
     */
    @Override
    public T queryForId(Connection connection, ID id) throws SQLException {
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();
        DBFieldType dbFieldType = tableInfo.getPrimaryKeys().get();
        Select query = Select.buildQueryById(tableInfo.getTableName(), dbFieldType, id);

        try (PreparedQueryImpl preparedQuery = new PreparedQueryImpl(connection.prepareStatement(getQuery(query)))) {
            try (DatabaseResults databaseResults = preparedQuery.executeQuery()) {
                if (databaseResults.next()) {
                    return dataBaseObject.getObjectBuilder().newObject()
                            .buildBase(databaseResults, tableInfo.toDBFieldTypes())
                            .buildForeign(databaseResults, tableInfo.toForeignFieldTypes())
                            .buildForeignCollection(tableInfo.toForeignCollectionFieldTypes())
                            .build();
                }
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }

        return null;
    }

    /**
     * Возвращает все объекты из таблицы
     * Выполняет запрос вида SELECT * FROM ...
     */
    @Override
    public List<T> queryForAll(Connection connection) throws SQLException {
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();
        Select select = Select.buildQueryForAll(tableInfo.getTableName());
        List<T> resultObjectList = new ArrayList<>();

        try (PreparedQueryImpl preparedQueryImpl = new PreparedQueryImpl(connection.prepareStatement(getQuery(select)))) {
            try (DatabaseResults databaseResults = preparedQueryImpl.executeQuery()) {
                while (databaseResults.next()) {
                    resultObjectList.add(
                            dataBaseObject.getObjectBuilder().newObject()
                                    .buildBase(databaseResults, tableInfo.toDBFieldTypes())
                                    .buildForeign(databaseResults, tableInfo.toForeignFieldTypes())
                                    .buildForeignCollection(tableInfo.toForeignCollectionFieldTypes())
                                    .build()
                    );
                }
            } catch (Exception ex) {
                throw new SQLException(ex);
            }

        }

        return resultObjectList;
    }


    @Override
    public void createIndexes(Connection connection) throws SQLException {
        List<IndexFieldType> indexFieldTypes = dataBaseObject.getTableInfo().getIndexFieldTypes();

        for (IndexFieldType indexFieldType : indexFieldTypes) {
            CreateIndexQuery createIndexQuery = CreateIndexQuery.build(indexFieldType);

            try (PreparedQueryImpl preparedQuery = new PreparedQueryImpl(connection.prepareStatement(getQuery(createIndexQuery)))) {
                preparedQuery.executeUpdate();
            }
        }
    }

    @Override
    public void dropIndexes(Connection connection) throws SQLException {
        List<IndexFieldType> indexFieldTypes = dataBaseObject.getTableInfo().getIndexFieldTypes();

        for (IndexFieldType indexFieldType : indexFieldTypes) {
            DropIndexQuery dropIndexQuery = DropIndexQuery.build(indexFieldType.getName());

            try (PreparedQueryImpl preparedQuery = new PreparedQueryImpl(connection.prepareStatement(getQuery(dropIndexQuery)))) {
                preparedQuery.executeUpdate();
            }
        }
    }

    @Override
    public<R> GenericResults<R> query(String query, ResultsMapper<R> resultsMapper, Connection connection) throws SQLException {
        try (PreparedQueryImpl preparedQueryImpl = new PreparedQueryImpl(connection.prepareStatement(query))) {
            try (DatabaseResults databaseResults = preparedQueryImpl.executeQuery()) {
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
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    @Override
    public long query(String query, Connection connection) throws SQLException {
        try (PreparedQueryImpl preparedQueryImpl = new PreparedQueryImpl(connection.prepareStatement(query))) {
            try (DatabaseResults databaseResults = preparedQueryImpl.executeQuery()) {
                if (databaseResults.next()) {
                    return databaseResults.getLong(0);
                }
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }

        throw new SQLException("No result found in queryForLong: " + query);
    }

    private String getQuery(QueryElement queryElement) {
        DefaultVisitor defaultVisitor = new DefaultVisitor(dataBaseObject.getDataSource().getDatabaseType());

        queryElement.accept(defaultVisitor);

        return defaultVisitor.getQuery();
    }
}
