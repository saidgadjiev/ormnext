package ru.said.miami.orm.core.field;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Arrays;

public class DBFieldType {

    private static final String ID_SUFFIX = "_id";

    private String fieldName;

    private DataType dataType;

    private boolean foreign;

    private Field field;

    private int length;

    private boolean id;

    private boolean generated;

    private DataPersister dataPersister;

    private boolean foreignAutoCreate;

    private Field foreignIdField;

    private Class<?> foreignFieldClass;

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

    public boolean isForeignAutoCreate() {
        return foreignAutoCreate;
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

    public boolean isForeign() {
        return foreign;
    }

    public Field getField() {
        return field;
    }

    public Class<?> getForeignFieldType() {
        return foreignFieldClass;
    }

    public static DBFieldType buildFieldType(Field field) throws NoSuchMethodException, SQLException {
        DBField dbField = field.getAnnotation(DBField.class);
        DBFieldType fieldType = new DBFieldType();

        fieldType.field = field;
        fieldType.fieldName = dbField.fieldName().isEmpty() ? field.getName().toLowerCase() : dbField.fieldName();
        fieldType.length = dbField.length();
        fieldType.id = dbField.id();
        fieldType.generated = dbField.generated();

        if (dbField.foreign()) {
            fieldType.foreignAutoCreate = dbField.foreignAutoCreate();
            fieldType.foreign = dbField.foreign();
            collectForeignInfo(fieldType, field);
        } else {
            fieldType.dataPersister = DataPersisterManager.lookupField(field);
            fieldType.dataType = dbField.dataType().equals(DataType.UNKNOWN) ? fieldType.dataPersister.getDataType() : dbField.dataType();
        }

        return fieldType;
    }

    private static void collectForeignInfo(DBFieldType fieldType, Field field) throws NoSuchMethodException, SQLException {
        fieldType.fieldName += ID_SUFFIX;
        fieldType.foreignIdField = findIdField(field.getType());
        fieldType.foreignFieldClass = fieldType.foreignIdField.getDeclaringClass();
        fieldType.dataPersister = DataPersisterManager.lookupField(fieldType.foreignIdField);
        fieldType.dataType = fieldType.dataPersister.getDataType();
    }

    private static Field findIdField(Class<?> clazz) throws SQLException {
        return Arrays.stream(clazz.getDeclaredFields()).filter(field -> {
            if (field.isAnnotationPresent(DBField.class)) {
                DBField dbField = field.getAnnotation(DBField.class);

                return dbField.id();
            }

            return false;
        }).findAny().orElseThrow(() -> new SQLException(""));
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "FieldType{" +
                "fieldName='" + fieldName + '\'' +
                ", dataType=" + dataType +
                '}';
    }
}
