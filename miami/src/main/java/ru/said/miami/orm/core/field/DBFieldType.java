package ru.said.miami.orm.core.field;

import ru.said.miami.orm.core.table.TableInfo;

import java.lang.reflect.Field;
import java.util.Optional;

public class DBFieldType {

    private static final String ID_SUFFIX = "_id";

    private String fieldName;

    private DataType dataType;

    private Class<?> foreignFieldType;

    private boolean foreign;

    private Field field;

    private int length;

    private boolean id;

    private boolean generated;

    private DataPersister dataPersister;

    private TableInfo<?> foreignTableInfo;

    private boolean foreignAutoCreate;

    private boolean foreignCollection;

    private String foreignFieldName;

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

    public TableInfo<?> getForeignTableInfo() {
        return foreignTableInfo;
    }

    public Field getField() {
        return field;
    }

    public Class<?> getForeignFieldType() {
        return foreignFieldType;
    }

    public static DBFieldType buildFieldType(Field field) throws NoSuchMethodException {
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
                fieldType.foreignFieldType = field.getType();
                collectForeignInfo(fieldType);
            } else {
                fieldType.dataPersister = DataPersisterManager.lookupField(field);
                fieldType.dataType = dbField.dataType().equals(DataType.UNKNOWN) ? fieldType.dataPersister.getDataType() : dbField.dataType();
            }

            return fieldType;

    }

    private static void collectForeignInfo(DBFieldType fieldType) throws NoSuchMethodException {
        fieldType.fieldName += ID_SUFFIX;
        fieldType.foreignTableInfo = TableInfo.buildTableInfo(fieldType.foreignFieldType);
        if (fieldType.foreignTableInfo.getIdField().isPresent()) {
            fieldType.dataPersister = DataPersisterManager.lookupField(fieldType.foreignTableInfo.getIdField().get().getField());
            fieldType.dataType = fieldType.dataPersister.getDataType();
        }
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
