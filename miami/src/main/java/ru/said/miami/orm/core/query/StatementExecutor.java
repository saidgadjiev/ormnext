package ru.said.miami.orm.core.query;

import ru.said.miami.orm.core.field.FieldType;
import ru.said.miami.orm.core.query.core.*;
import ru.said.miami.orm.core.table.TableInfo;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс для выполнения sql запросов
 *
 * @param <T>  тип объекта
 * @param <ID> id объекта
 */
public class StatementExecutor<T, ID> {

    private TableInfo<T> tableInfo;

    public StatementExecutor(TableInfo<T> tableInfo) {

        this.tableInfo = tableInfo;
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида INSERT INTO ...(colname1, colname2, ...) VALUES(colvalue1, colvalue2, ...)
     */
    @SuppressWarnings("unchecked")
    public int create(Connection connection, T object) throws SQLException {
        CreateQuery query = CreateQuery.buildQuery(
                tableInfo.getTableName(),
                tableInfo.getFieldTypes().stream()
                        .filter(fieldType -> !(fieldType.isId() && fieldType.isGenerated()))
                        .collect(Collectors.toList()),
                object);
        Integer result;

        if ((result = query.execute(connection)) != null) {
            if (tableInfo.getIdField().isPresent()) {
                FieldType idField = tableInfo.getIdField().get();
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

    public boolean createTable(Connection connection) throws SQLException {
        CreateTableQuery createTableQuery = CreateTableQuery.buildQuery(tableInfo.getTableName(), tableInfo.getFieldTypes());

        return createTableQuery.execute(connection);
    }

    /**
     * Обновляет объект в базе
     * Выполняет запрос вида UPDATE ... SET colname1 = colvalue1 SET colname2 = colvalue2 WHERE = object.id
     */
    public int update(Connection connection, T object) throws SQLException {
        if (tableInfo.getIdField().isPresent()) {
            Query query = UpdateQuery.buildQuery(tableInfo.getTableName(), tableInfo.getFieldTypes(), tableInfo.getIdField().get(), object);
            Integer result;

            if ((result = query.execute(connection)) != null) {
                return result;
            }
        }

        throw new SQLException("Id field not defined. Can't update object");
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида DELETE FROM ... WHERE = object.id
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

        throw new SQLException("Id field not defined. Can't delete object");
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида DELETE FROM ... WHERE = object.id
     */
    public int deleteById(Connection connection, ID id) throws SQLException {
        if (tableInfo.getIdField().isPresent()) {
            Query query = DeleteQuery.buildQuery(tableInfo.getTableName(), tableInfo.getIdField().get(), id);
            Integer result;

            if ((result = query.execute(connection)) != null) {
                return result;
            }
        }

        throw new SQLException("Id field not defined. Can't delete object");
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
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException ex) {
                throw new SQLException(ex);
            }
        }

        throw new SQLException("Id field not defined. Can't select object");
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
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException ex) {
            throw new SQLException(ex);
        }

        return resultObjectList;
    }

    private T mapResult(IMiamiData data) throws SQLException, InvocationTargetException, IllegalAccessException, InstantiationException {
        //TODO: Возможно стоит вынести эту логику в отдельный класс
        T resultObject = tableInfo.getConstructor().newInstance();

        for (FieldType fieldType : tableInfo.getFieldTypes()) {
            fieldType.assignField(resultObject, data.getObject(fieldType.getFieldName()));
        }

        return resultObject;
    }
}
