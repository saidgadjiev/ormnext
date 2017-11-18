package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.field.fieldTypes.DBFieldType;
import ru.said.miami.orm.core.query.core.common.UpdateValue;
import ru.said.miami.orm.core.stamentExecutor.FieldConverter;
import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс INSERT запроса
 */
public class CreateQuery implements QueryElement {

    /**
     * Название типа
     */
    private final String typeName;

    /**
     * Вставляемые значения
     */
    private final List<UpdateValue> updateValues = new ArrayList<>();


    private CreateQuery(String typeName) {
        this.typeName = typeName;
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

    public static<T> CreateQuery buildQuery(String typeName, List<DBFieldType> fieldTypes, T object) throws SQLException {
        CreateQuery createQuery = new CreateQuery(typeName);

        try {
            if (fieldTypes != null && object != null) {
                for (DBFieldType fieldType : fieldTypes) {
                    createQuery.updateValues.add(
                            new UpdateValue(
                                    fieldType.getColumnName(), FieldConverter.getInstanse().convert(fieldType.getDataType(), fieldType.access(object)))
                    );
                }
            }
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new SQLException(ex);
        }

        return createQuery;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.start(this)) {
            for (UpdateValue updateValue : updateValues) {
                updateValue.accept(visitor);
            }
        }
        visitor.finish(this);
    }
}
