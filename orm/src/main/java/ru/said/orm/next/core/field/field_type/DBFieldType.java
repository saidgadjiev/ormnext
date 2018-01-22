package ru.said.orm.next.core.field.field_type;

import ru.said.orm.next.core.field.DBField;
import ru.said.orm.next.core.field.DataPersisterManager;
import ru.said.orm.next.core.field.DataType;
import ru.said.orm.next.core.field.FieldAccessor;
import ru.said.orm.next.core.field.persisters.DataPersister;
import ru.said.orm.next.core.field.persisters.FieldConverter;
import ru.said.up.cache.core.Cache;
import ru.said.up.cache.core.CacheBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class DBFieldType implements IDBFieldType {

    private String columnName;

    private DataType dataType;

    private Field field;

    private int length;

    private DataPersister<?> dataPersister;

    private FieldConverter<?> fieldConverter;

    private FieldAccessor fieldAccessor;

    private boolean notNull;

    private boolean id;

    private boolean generated;

    private Object defaultValue;

    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean isId() {
        return id;
    }

    @Override
    public boolean isNotNull() {
        return notNull;
    }

    @Override
    public boolean isGenerated() {
        return generated;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }

    @Override
    public DataType getDataType() {
        return dataType;
    }

    @Override
    public Object access(Object object) throws InvocationTargetException, IllegalAccessException {
        return fieldAccessor.access(object);
    }

    @Override
    public DataPersister getDataPersister() {
        return dataPersister;
    }

    @Override
    public void assign(Object object, Object value) throws IllegalAccessException, InvocationTargetException {
        fieldAccessor.assign(object, value);
    }

    @Override
    public Field getField() {
        return field;
    }

    public static DBFieldType build(Field field) throws NoSuchMethodException {
        DBField dbField = field.getAnnotation(DBField.class);
        DBFieldType fieldType = new DBFieldType();

        fieldType.field = field;
        fieldType.columnName = dbField.columnName().isEmpty() ? field.getName().toLowerCase() : dbField.columnName();
        fieldType.length = dbField.length();
        fieldType.fieldAccessor = new FieldAccessor(field);
        if (!dbField.foreign()) {
            DataType dataType = dbField.dataType();

            fieldType.dataPersister = dataType.equals(DataType.UNKNOWN) ? DataPersisterManager.lookup(field.getType()) : dataType.getDataPersister();
            fieldType.dataType = fieldType.dataPersister.getDataType();
            fieldType.fieldConverter = fieldType.dataPersister;
            if (!dbField.defaultValue().isEmpty()) {
                fieldType.defaultValue = fieldType.fieldConverter.convertTo(dbField.defaultValue());
            }
        }
        fieldType.notNull = dbField.notNull();
        fieldType.id = dbField.id();
        fieldType.generated = dbField.generated();

        return fieldType;
    }

    @Override
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

    public static class DBFieldTypeCache {

        private static final Cache<Field, DBFieldType> CACHE = CacheBuilder.newRefenceCacheBuilder().build();

        public static DBFieldType build(Field field) throws NoSuchMethodException, NoSuchFieldException {
            if (CACHE.contains(field)) {
                return CACHE.get(field);
            }
            DBFieldType dbFieldType = DBFieldType.build(field);

            CACHE.put(field, dbFieldType);

            return dbFieldType;
        }
    }
}
