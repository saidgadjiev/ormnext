package ru.said.miami.orm.core.field;

import ru.said.miami.cache.core.Cache;
import ru.said.miami.cache.core.CacheBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;

public class DBFieldType {

    private static final String ID_SUFFIX = "_id";

    private String columnName;

    private DataType dataType;

    private boolean foreign;

    private Field field;

    private int length;

    private boolean id;

    private boolean generated;

    private DataPersister dataPersister;

    private boolean foreignAutoCreate;

    private Field foreignIdField;

    private FieldAccessor fieldAccessor;

    private Class<?> foreignFieldClass;

    public String getColumnName() {
        return columnName;
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

    public Object access(Object object) throws InvocationTargetException, IllegalAccessException {
        return fieldAccessor.access(object);
    }

    public boolean isForeignAutoCreate() {
        return foreignAutoCreate;
    }

    public DataPersister getDataPersister() {
        return dataPersister;
    }

    public void assign(Object object, Object value) throws IllegalAccessException, InvocationTargetException {
        fieldAccessor.assign(object, value);
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

    public static DBFieldType build(Field field) throws NoSuchMethodException, NoSuchFieldException {
        DBField dbField = field.getAnnotation(DBField.class);
        DBFieldType fieldType = new DBFieldType();

        fieldType.field = field;
        fieldType.columnName = dbField.columnName().isEmpty() ? field.getName().toLowerCase() : dbField.columnName();
        fieldType.length = dbField.length();
        fieldType.id = dbField.id();
        fieldType.fieldAccessor = new FieldAccessor(field);
        fieldType.generated = dbField.generated();

        if (dbField.foreign()) {
            fieldType.foreignAutoCreate = dbField.foreignAutoCreate();
            fieldType.foreign = dbField.foreign();
            fieldType.columnName += ID_SUFFIX;
            fieldType.foreignIdField = findIdField(field.getType()).orElseThrow(() -> new NoSuchFieldException("Foreign id field is not defined"));
            fieldType.foreignFieldClass = fieldType.foreignIdField.getDeclaringClass();
            fieldType.dataPersister = DataPersisterManager.lookupField(fieldType.foreignIdField);
            fieldType.dataType = fieldType.dataPersister.getDataType();
        } else {
            fieldType.dataPersister = DataPersisterManager.lookupField(field);
            fieldType.dataType = dbField.dataType().equals(DataType.UNKNOWN) ? fieldType.dataPersister.getDataType() : dbField.dataType();
        }

        return fieldType;
    }

    private static Optional<Field> findIdField(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields()).filter(field -> {
            if (field.isAnnotationPresent(DBField.class)) {
                DBField dbField = field.getAnnotation(DBField.class);

                return dbField.id();
            }

            return false;
        }).findFirst();
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "FieldType{" +
                "columnName='" + columnName + '\'' +
                ", dataType=" + dataType +
                '}';
    }
}
