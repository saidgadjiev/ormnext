package ru.said.miami.orm.core.field.fieldTypes;

import ru.said.miami.cache.core.Cache;
import ru.said.miami.cache.core.CacheBuilder;
import ru.said.miami.orm.core.field.DBField;
import ru.said.miami.orm.core.field.DataPersisterManager;
import ru.said.miami.orm.core.field.DataType;
import ru.said.miami.orm.core.field.FieldAccessor;
import ru.said.miami.orm.core.field.persisters.DataPersister;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class DBFieldType {

    private String columnName;

    private DataType dataType;

    private Field field;

    private int length;

    private DataPersister dataPersister;

    private FieldAccessor fieldAccessor;

    private boolean notNull;

    private boolean id;

    private boolean generated;

    public boolean isId() {
        return id;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public boolean isGenerated() {
        return generated;
    }

    public String getColumnName() {
        return columnName;
    }

    public DataType getDataType() {
        return dataType;
    }

    public Object access(Object object) throws InvocationTargetException, IllegalAccessException {
        return fieldAccessor.access(object);
    }

    public DataPersister getDataPersister() {
        return dataPersister;
    }

    public void assign(Object object, Object value) throws IllegalAccessException, InvocationTargetException {
        fieldAccessor.assign(object, value);
    }

    public Field getField() {
        return field;
    }

    public static DBFieldType build(Field field) throws NoSuchMethodException, NoSuchFieldException {
        DBField dbField = field.getAnnotation(DBField.class);
        DBFieldType fieldType = new DBFieldType();

        fieldType.field = field;
        fieldType.columnName = dbField.columnName().isEmpty() ? field.getName().toLowerCase() : dbField.columnName();
        fieldType.length = dbField.length();
        fieldType.fieldAccessor = new FieldAccessor(field);
        if (!dbField.foreign()) {
            fieldType.dataPersister = DataPersisterManager.lookup(field);
            fieldType.dataType = dbField.dataType().equals(DataType.UNKNOWN) ? fieldType.dataPersister.getDataType() : dbField.dataType();
        }
        fieldType.notNull = dbField.notNull();
        fieldType.id = dbField.id();
        fieldType.generated = dbField.generated();

        return fieldType;
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
