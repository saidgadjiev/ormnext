package ru.said.miami.orm.core.query;

import ru.said.miami.orm.core.cache.ObjectCache;
import ru.said.miami.orm.core.field.DBFieldType;
import ru.said.miami.orm.core.query.core.*;
import ru.said.miami.orm.core.query.core.object.DataBaseObject;
import ru.said.miami.orm.core.table.TableInfo;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для выполнения sql запросов
 *
 * @param <T>  тип объекта
 * @param <ID> id объекта
 */
public class StatementExecutor<T, ID> {

    private DataBaseObject<T> dataBaseObject;

    //TODO: Вынести работу с кешом в отдельный класс
    private ObjectCache objectCache;

    public StatementExecutor(DataBaseObject<T> dataBaseObject) {
        this.dataBaseObject = dataBaseObject;
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида INSERT INTO ...(colname1, colname2, ...) VALUES(colvalue1, colvalue2, ...)
     */
    @SuppressWarnings("unchecked")
    public int create(Connection connection, T object) throws SQLException {
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();

        try {
            CreateQuery query = dataBaseObject.getObjectCreator().newObject(object)
                    .createBase(object)
                    .createForeign(object)
                    .query();
            Integer result = query.execute(connection);

            //TODO: можно вынести например в ObjectBuilder
            if (tableInfo.getIdField().isPresent()) {
                DBFieldType idField = tableInfo.getIdField().get();
                Number generatedKey = query.getGeneratedKey().orElseThrow(() -> new SQLException("Запрос не вернул автоинкриментных ключей"));

                try {
                    idField.assign(object, generatedKey);
                } catch (IllegalAccessException ex) {
                    throw new SQLException(ex);
                }
            }

            return result;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    public boolean createTable(Connection connection) throws SQLException {
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();

        CreateTableQuery createTableQuery = CreateTableQuery.buildQuery(
                tableInfo.getTableName(),
                tableInfo.toDBFieldTypes()
        );

        return createTableQuery.execute(connection);
    }

    /**
     * Обновляет объект в базе
     * Выполняет запрос вида UPDATE ... SET colname1 = colvalue1 SET colname2 = colvalue2 WHERE = object_builder.id
     */
    public int update(Connection connection, T object) throws SQLException {
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();
        DBFieldType idFieldType = tableInfo.getIdField().orElseThrow(() -> new SQLException("Id is not defined"));
        Query<Integer> query = UpdateQuery.buildQuery(
                tableInfo.getTableName(),
                tableInfo.toDBFieldTypes(),
                idFieldType,
                object
        );

        try {
            T cachedData = objectCache.get(idFieldType.access(object));

            //TODO: update cachedData
        } catch (Exception ex) {
            throw new SQLException(ex);
        }

        return query.execute(connection);
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида DELETE FROM ... WHERE = object_builder.id
     */
    public int delete(Connection connection, T object) throws SQLException {
        try {
            TableInfo<T> tableInfo = dataBaseObject.getTableInfo();
            DBFieldType dbFieldType = tableInfo.getIdField().orElseThrow(() -> new SQLException("Id is not defined"));
            Object id = dbFieldType.access(object);
            Query<Integer> query = DeleteQuery.buildQuery(tableInfo.getTableName(), dbFieldType, id);
            Integer result = query.execute(connection);

            objectCache.remove(id);

            return result;
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида DELETE FROM ... WHERE = object_builder.id
     */
    public int deleteById(Connection connection, ID id) throws SQLException {
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();
        DBFieldType dbFieldType = tableInfo.getIdField().orElseThrow(() -> new SQLException("Id is not defined"));
        Query<Integer> query = DeleteQuery.buildQuery(tableInfo.getTableName(), dbFieldType, id);
        Integer result = query.execute(connection);

        objectCache.remove(id);

        return result;
    }

    /**
     * Возвращает объект по id
     * Выполняет запрос вида SELECT * FROM ... WHERE = id
     */
    public T queryForId(Connection connection, ID id) throws SQLException {
        if (objectCache.contains(id)) {
            return objectCache.get(id);
        }

        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();
        DBFieldType dbFieldType = tableInfo.getIdField().orElseThrow(() -> new SQLException("Id is not defined"));
        SelectQuery query = SelectQuery.buildQueryById(tableInfo.getTableName(), dbFieldType, id);

        try (IMiamiCollection result = query.execute(connection)) {
            if (result.next()) {
                IMiamiData data = result.get();
                T resultObject = dataBaseObject.getObjectBuilder().newObject()
                        .buildBase(data)
                        .buildForeign(data)
                        .buildForeignCollection()
                        .build();

                objectCache.put(id, resultObject);
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
    public List<T> queryForAll(Connection connection) throws SQLException {
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();
        Query<IMiamiCollection> query = SelectQuery.buildQueryForAll(tableInfo.getTableName());
        List<T> resultObjectList = new ArrayList<>();

        try (IMiamiCollection result = query.execute(connection)) {
            while (result.next()) {
                IMiamiData data = result.get();

                resultObjectList.add(
                        dataBaseObject.getObjectBuilder().newObject()
                                .buildBase(data)
                                .buildForeign(data)
                                .buildForeignCollection()
                                .build()
                );
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }

        return resultObjectList;
    }

    public <R> R execute(Query<R> query, Connection connection) throws SQLException {
        return query.execute(connection);
    }
}
