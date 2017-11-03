package ru.said.miami.orm.core.query.stamentExecutor;

import ru.said.miami.orm.core.field.DBFieldType;
import ru.said.miami.orm.core.field.IndexFieldType;
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
            Integer result = query.execute(connection);

            //TODO: можно вынести например в ObjectBuilder
            if (tableInfo.getPrimaryKeys().isPresent()) {
                DBFieldType idField = tableInfo.getPrimaryKeys().get();
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

    @Override
    public boolean createTable(Connection connection) throws SQLException {
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();
        CreateTableQuery createTableQuery = CreateTableQuery.buildQuery(
                tableInfo
        );

        return createTableQuery.execute(connection);
    }

    @Override
    public boolean dropTable(Connection connection) throws SQLException {
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();
        DropTableQuery dropTableQuery = DropTableQuery.buildQuery(tableInfo.getTableName());

        return dropTableQuery.execute(connection);
    }

    /**
     * Обновляет объект в базе
     * Выполняет запрос вида UPDATE ... SET colname1 = colvalue1 SET colname2 = colvalue2 WHERE = object_builder.id
     */
    @Override
    public int update(Connection connection, T object) throws SQLException {
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();
        DBFieldType idFieldType = tableInfo.getPrimaryKeys().get();
        Query<Integer> query = UpdateQuery.buildQuery(
                tableInfo.getTableName(),
                tableInfo.toDBFieldTypes(),
                idFieldType,
                object
        );

        return query.execute(connection);
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
            Query<Integer> query = DeleteQuery.buildQuery(tableInfo.getTableName(), dbFieldType, id);

            return query.execute(connection);
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
        Query<Integer> query = DeleteQuery.buildQuery(tableInfo.getTableName(), dbFieldType, id);

        return query.execute(connection);
    }

    /**
     * Возвращает объект по id
     * Выполняет запрос вида SELECT * FROM ... WHERE = id
     */
    @Override
    public T queryForId(Connection connection, ID id) throws SQLException {
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();
        DBFieldType dbFieldType = tableInfo.getPrimaryKeys().get();
        SelectQuery query = SelectQuery.buildQueryById(tableInfo.getTableName(), dbFieldType, id);

        try (IMiamiCollection result = query.execute(connection)) {
            if (result.next()) {
                IMiamiData data = result.get();
                return dataBaseObject.getObjectBuilder().newObject()
                        .buildBase(data)
                        .buildForeign(data)
                        .buildForeignCollection()
                        .build();
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

    @Override
    public <R> R execute(Query<R> query, Connection connection) throws SQLException {
        return query.execute(connection);
    }

    @Override
    public void createIndexes(Connection connection) throws SQLException {
        List<IndexFieldType> indexFieldTypes = dataBaseObject.getTableInfo().getIndexFieldTypes();

        for (IndexFieldType indexFieldType: indexFieldTypes) {
            CreateIndexQuery createIndexQuery = CreateIndexQuery.build(indexFieldType);

            createIndexQuery.execute(connection);
        }
    }

    @Override
    public void dropIndexes(Connection connection) throws SQLException {
        List<IndexFieldType> indexFieldTypes = dataBaseObject.getTableInfo().getIndexFieldTypes();

        for (IndexFieldType indexFieldType: indexFieldTypes) {
            DropIndexQuery dropIndexQuery = DropIndexQuery.build(indexFieldType.getName());

            dropIndexQuery.execute(connection);
        }
    }
}
