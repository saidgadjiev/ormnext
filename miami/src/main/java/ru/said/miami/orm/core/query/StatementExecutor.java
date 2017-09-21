package ru.said.miami.orm.core.query;

import ru.said.miami.orm.core.field.FieldType;
import ru.said.miami.orm.core.query.core.*;
import ru.said.miami.orm.core.table.TableInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс для выполнения sql запросов
 * @param <T> тип объекта
 * @param <ID> id объекта
 */
public class StatementExecutor<T, ID> {

    private TableInfo tableInfo;

    public StatementExecutor(TableInfo tableInfo) {

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
                Long key = query.getGeneratedKey();

                try {
                    idField.assignField(object, key);
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
        Query query = UpdateQuery.buildQuery(tableInfo.getTableName());
        Integer result;

        if ((result = query.execute(connection)) != null) {
            return result;
        }

        return 0;
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида DELETE FROM ... WHERE = object.id
     */
    public int delete(Connection connection, T object) throws SQLException {
        Query query = DeleteQuery.buildQuery();
        Integer result;

        if ((result = query.execute(connection)) != null) {
            return result;
        }

        return 0;
    }

    /**
     * Возвращает объект по id
     * Выполняет запрос вида SELECT * FROM ... WHERE = id
     */
    public T queryForId(Connection connection, ID id) throws SQLException {
        Query query = SelectQuery.buildQuery();
        T result;

        if ((result = query.execute(connection)) != null) {
            return result;
        }

        return null;
    }

    /**
     * Возвращает все объекты из таблицы
     * Выполняет запрос вида SELECT * FROM ...
     */
    public List<T> queryForAll(Connection connection) throws SQLException {
        Query query = SelectQuery.buildQuery();
        List<T> result;

        if ((result = query.execute(connection)) != null) {
            return result;
        }

        return null;
    }

}
