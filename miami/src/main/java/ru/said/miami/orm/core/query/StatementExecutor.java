package ru.said.miami.orm.core.query;

import ru.said.miami.orm.core.dao.BaseDaoImpl;
import ru.said.miami.orm.core.dao.Dao;
import ru.said.miami.orm.core.dao.DaoManager;
import ru.said.miami.orm.core.field.DBFieldType;
import ru.said.miami.orm.core.query.core.*;
import ru.said.miami.orm.core.query.core.object_builder.*;
import ru.said.miami.orm.core.table.TableInfo;

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

    private TableInfo<T> tableInfo;

    private Dao<T, ID> dao;

    private ObjectBuilderChain<T> objectBuilderChain;

    public StatementExecutor(TableInfo<T> tableInfo, Dao<T, ID> dao) {
        this.dao = dao;
        this.tableInfo = tableInfo;
        this.objectBuilderChain = new ObjectBuilderChain<>(dao.getDataSource(), tableInfo);
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида INSERT INTO ...(colname1, colname2, ...) VALUES(colvalue1, colvalue2, ...)
     */
    @SuppressWarnings("unchecked")
    public int create(Connection connection, T object) throws SQLException {
        CreateQuery query = CreateQuery.buildQuery(tableInfo.getTableName(), null, null);

        try {
            for (DBFieldType fieldType : tableInfo.toDBFieldTypes()) {
                if (fieldType.isId() && fieldType.isGenerated()) {
                    continue;
                }
                Object value = null;

                if (fieldType.isForeign()) {
                    Object foreignObject = fieldType.getValue(object);
                    TableInfo<?> foreignTableInfo = TableInfo.buildTableInfo(fieldType.getForeignFieldType());

                    if (fieldType.isForeignAutoCreate()) {
                        Dao<Object, ?> foreignDao = (BaseDaoImpl<Object, ?>) DaoManager.createDAOWithTableInfo(dao.getDataSource(), foreignTableInfo);

                        foreignDao.create(foreignObject);
                    }

                    if (foreignTableInfo.getIdField().isPresent()) {
                        value = foreignTableInfo.getIdField().get().getValue(foreignObject);
                    }
                } else {
                    value = fieldType.getValue(object);
                }
                query.add(new UpdateValue(
                        fieldType.getFieldName(),
                        FieldConverter.getInstanse().convert(fieldType.getDataType(), value))
                );
            }
        } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException ex) {
            throw new SQLException(ex);
        }
        Integer result;

        if ((result = query.execute(connection)) != null) {
            if (tableInfo.getIdField().isPresent()) {
                DBFieldType idField = tableInfo.getIdField().get();
                Number generatedKey = query.getGeneratedKey().orElseThrow(() -> new SQLException("Запрос не вернул автоинкриментных ключей"));

                try {
                    idField.assignField(object, generatedKey);
                } catch (IllegalAccessException ex) {
                    throw new SQLException(ex);
                }
            }

            return result;
        }

        return 0;
    }

    private void assignIdField(DBFieldType idField, Object value) {

    }

    public boolean createTable(Connection connection) throws SQLException {
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
        if (tableInfo.getIdField().isPresent()) {
            Query query = UpdateQuery.buildQuery(
                    tableInfo.getTableName(),
                    tableInfo.toDBFieldTypes(),
                    tableInfo.getIdField().get(),
                    object
            );
            Integer result;

            if ((result = query.execute(connection)) != null) {
                return result;
            }
        }

        throw new SQLException("Id field not defined. Can't update object_builder");
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида DELETE FROM ... WHERE = object_builder.id
     */
    public int delete(Connection connection, T object) throws SQLException {
        if (tableInfo.getIdField().isPresent()) {
            try {
                Query query = DeleteQuery.buildQuery(tableInfo.getTableName(), tableInfo.getIdField().get(), tableInfo.getIdField().get().getValue(object));
                Integer result;

                if ((result = query.execute(connection)) != null) {
                    return result;
                }
            } catch (IllegalAccessException ex) {
                throw new SQLException(ex);
            }
        }

        throw new SQLException("Id field not defined. Can't delete object_builder");
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида DELETE FROM ... WHERE = object_builder.id
     */
    public int deleteById(Connection connection, ID id) throws SQLException {
        if (tableInfo.getIdField().isPresent()) {
            Query query = DeleteQuery.buildQuery(tableInfo.getTableName(), tableInfo.getIdField().get(), id);
            Integer result;

            if ((result = query.execute(connection)) != null) {
                return result;
            }
        }

        throw new SQLException("Id field not defined. Can't delete object_builder");
    }

    /**
     * Возвращает объект по id
     * Выполняет запрос вида SELECT * FROM ... WHERE = id
     */
    public T queryForId(Connection connection, ID id) throws SQLException {
        if (tableInfo.getIdField().isPresent()) {
            SelectQuery query = SelectQuery.buildQueryById(tableInfo.getTableName(), tableInfo.getIdField().get(), id);

            try (IMiamiCollection result = query.execute(connection)) {
                if (result.next()) {
                    return mapResult(result.get());
                }
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }

        throw new SQLException("Id field not defined. Can't select object_builder");
    }

    /**
     * Возвращает все объекты из таблицы
     * Выполняет запрос вида SELECT * FROM ...
     */
    public List<T> queryForAll(Connection connection) throws SQLException {
        Query query = SelectQuery.buildQueryForAll(tableInfo.getTableName());
        List<T> resultObjectList = new ArrayList<>();

        try (IMiamiCollection result = query.execute(connection)) {
            while (result.next()) {
                resultObjectList.add(mapResult(result.get()));
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }

        return resultObjectList;
    }

    //TODO: стоит пересмотреть сигнатуру метода и вынести в отдельный интерфейс
    private T mapResult(IMiamiData data) throws Exception {
        return objectBuilderChain.build(data);
    }
}
