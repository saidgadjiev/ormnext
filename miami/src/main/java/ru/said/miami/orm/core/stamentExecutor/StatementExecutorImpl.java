package ru.said.miami.orm.core.stamentExecutor;

import ru.said.miami.orm.core.field.fieldTypes.DBFieldType;
import ru.said.miami.orm.core.field.fieldTypes.IndexFieldType;
import ru.said.miami.orm.core.query.core.*;
import ru.said.miami.orm.core.query.visitor.DefaultVisitor;
import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.stamentExecutor.object.DataBaseObject;
import ru.said.miami.orm.core.queryBuilder.PreparedQuery;
import ru.said.miami.orm.core.table.TableInfo;

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
//TODO: снять валидацию с этого класса м б перенести в отдельный класс
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
                return dataBaseObject.getObjectBuilder().newObject()
                        .buildBase(databaseResults, null)
                        .buildForeign(databaseResults)
                        .buildForeignCollection()
                        .build();
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
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
                                    .buildBase(databaseResults, null)
                                    .buildForeign(databaseResults)
                                    .buildForeignCollection()
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
    public List<T> query(PreparedQuery preparedQuery, Connection connection) throws SQLException {
        List<T> resultObjectList = new ArrayList<>();

        try (PreparedQueryImpl preparedQueryImpl = preparedQuery.compile(connection)) {
            try (DatabaseResults databaseResults = preparedQueryImpl.executeQuery()) {
                while (databaseResults.next()) {
                    resultObjectList.add(
                            dataBaseObject.getObjectBuilder().newObject()
                                    .buildBase(databaseResults, preparedQuery.getResultFieldTypes())
                                    .buildForeign(databaseResults, preparedQuery.getResultFieldTypes())
                                    .buildForeignCollection(preparedQuery.getResultFieldTypes())
                                    .build()
                    );
                }
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }

        return resultObjectList;
    }

    private String getQuery(QueryElement queryElement) {
        DefaultVisitor defaultVisitor = new DefaultVisitor();

        queryElement.accept(defaultVisitor);

        return defaultVisitor.getQuery();
    }
}
