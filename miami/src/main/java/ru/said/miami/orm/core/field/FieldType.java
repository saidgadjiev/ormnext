package ru.said.miami.orm.core.field;

import java.lang.reflect.Field;
import java.sql.SQLException;

public class FieldType {

    private String fieldName;

    private DataType dataType;

    private Field field;

    private int length;

    private boolean id;

    private boolean generated;

    public String getFieldName() {
        return fieldName;
    }

    public DataType getDataType() {
        return dataType;
    }

    public boolean isId() {
        return id;
    }

    public boolean isGenerated() {
        return generated;
    }

    public Object getValue(Object object) throws SQLException {
        try {
            return field.get(object);
        } catch (IllegalAccessException ex) {
            throw new SQLException(ex);
        }
    }

    public static FieldType buildFieldType(Field field) {
        if (!field.isAnnotationPresent(DBField.class)) {
            return null;
        }
        DBField dbField = field.getAnnotation(DBField.class);
        FieldType fieldType = new FieldType();

        fieldType.field = field;
        fieldType.fieldName = dbField.fieldName().isEmpty() ? field.getName().toLowerCase(): dbField.fieldName();
        fieldType.dataType = dbField.dataType();
        fieldType.length = dbField.length();
        fieldType.id = dbField.id();
        fieldType.generated = dbField.generated();

        return fieldType;
    }

    public int getLength() {
        return length;
    }
}
