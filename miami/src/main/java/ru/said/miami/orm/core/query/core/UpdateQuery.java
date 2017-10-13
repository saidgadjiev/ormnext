package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.field.DBFieldType;
import ru.said.miami.orm.core.query.visitor.DefaultVisitor;
import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс UPDATE запроса
 * @param <T> тип результата выполнения
 */

public class UpdateQuery implements Query, QueryElement {

    /**
     * Название типа
     */
    private final String typeName;

    /**
     * Вставляемые значения
     */
    private final List<UpdateValue> updateValues = new ArrayList<>();

    private Expression where = new Expression();

    private QueryVisitor visitor;

    private UpdateQuery(String typeName, QueryVisitor visitor) {
        this.typeName = typeName;
        this.visitor = visitor;
    }

    /**
     * Добавление нового значения
     *
     * @param updateValue добавляемое значение
     */
    public void add(UpdateValue updateValue) {
        updateValues.add(updateValue);
    }

    /**
     * Добавление коллекции значений
     *
     * @param values
     */
    public void addAll(List<UpdateValue> values) {
        updateValues.addAll(values);
    }

    /**
     * Получение списка значений
     *
     * @return
     */
    public List<UpdateValue> getUpdateValues() {
        return updateValues;
    }

    /**
     * Получение имени типа
     *
     * @return
     */
    public String getTypeName() {
        return typeName;
    }

    public Expression getWhere() {
        return where;
    }

    public void setWhere(Expression where) {
        this.where = where;
    }

    @Override
    public Integer execute(Connection connection) throws SQLException {
        this.accept(visitor);
        String sql = visitor.getQuery();

        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(sql);
        }
    }

    /**
     * Создание экземпляра UpdateQuery
     * @return возвращет экземляр UpdateQuery
     */
    public static<T, ID> UpdateQuery buildQuery(String typeName, List<DBFieldType> fieldTypes, DBFieldType idField, T object) throws SQLException {
        UpdateQuery updateQuery = new UpdateQuery(typeName, new DefaultVisitor());

        try {
            for (DBFieldType fieldType : fieldTypes) {
                updateQuery.updateValues.add(
                        new UpdateValue(
                                fieldType.getFieldName(), FieldConverter.getInstanse().convert(fieldType.getDataType(), fieldType.getValue(object)))
                );
            }
            AndCondition andCondition = new AndCondition();

            andCondition.add(new Equals(new ColumnSpec(idField.getFieldName()), idField.getDataPersister().getAssociatedOperand(idField.getValue(object))));
            updateQuery.getWhere().getConditions().add(andCondition);
        } catch (IllegalAccessException ex) {
            throw new SQLException(ex);
        }

        return updateQuery;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.start(this)) {
            for (UpdateValue updateValue : updateValues) {
                updateValue.accept(visitor);
            }
        }
        where.accept(visitor);
        visitor.finish(this);
    }
}
