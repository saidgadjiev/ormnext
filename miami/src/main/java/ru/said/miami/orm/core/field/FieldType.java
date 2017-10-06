package ru.said.miami.orm.core.field;

import ru.said.miami.orm.core.query.core.Operand;

import java.lang.reflect.Field;
import java.util.Optional;

public class FieldType {

    private String fieldName;

    private DataType dataType;

    private Field field;

    private int length;

    private boolean id;

    private boolean generated;

    private DataPersister dataPersister;

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

    public Object getValue(Object object) throws IllegalAccessException {
        if (!field.isAccessible()) {
            field.setAccessible(true);

            Object result = field.get(object);

            field.setAccessible(false);

            return result;
        }

        return field.get(object);
    }

    public DataPersister getDataPersister() {
        return dataPersister;
    }

    public void assignField(Object object, Object value) throws IllegalAccessException {
        if (!field.isAccessible()) {
            field.setAccessible(true);
            field.set(object, value);
            field.setAccessible(false);
        } else {
            field.set(object, value);
        }
    }

    public static Optional<FieldType> buildFieldType(Field field) {
        if (!field.isAnnotationPresent(DBField.class)) {
            return Optional.empty();
        }
        DBField dbField = field.getAnnotation(DBField.class);
        FieldType fieldType = new FieldType();

        fieldType.field = field;
        fieldType.fieldName = dbField.fieldName().isEmpty() ? field.getName().toLowerCase() : dbField.fieldName();
        fieldType.dataPersister = DataPersisterManager.lookupField(field);
        fieldType.dataType = dbField.dataType().equals(DataType.UNKNOWN) ? fieldType.dataPersister.getDataType(): dbField.dataType();
        fieldType.length = dbField.length();
        fieldType.id = dbField.id();
        fieldType.generated = dbField.generated();

        return Optional.of(fieldType);
    }

    public int getLength() {
        return length;
    }
}
