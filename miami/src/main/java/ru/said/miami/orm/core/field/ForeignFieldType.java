package ru.said.miami.orm.core.field;

import ru.said.miami.orm.core.table.DBTable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by said on 30.10.17.
 */
public class ForeignFieldType {

    private static final String ID_SUFFIX = "_id";

    private DBFieldType dbFieldType;

    private PrimaryKeyFieldType foreignPrimaryKey;

    private boolean foreignAutoCreate;

    private Class<?> foreignFieldClass;

    private DataPersister dataPersister;

    private DataType dataType;


    public boolean isForeignAutoCreate() {
        return foreignAutoCreate;
    }

    public Class<?> getForeignFieldClass() {
        return foreignFieldClass;
    }

    public DataPersister getDataPersister() {
        return dataPersister;
    }

    public DataType getDataType() {
        return dataType;
    }

    public DBFieldType getDbFieldType() {
        return dbFieldType;
    }

    public PrimaryKeyFieldType getForeignPrimaryKey() {
        return foreignPrimaryKey;
    }

    public Object access(Object object) throws InvocationTargetException, IllegalAccessException {
        return dbFieldType.access(object);
    }

    public void assign(Object object, Object value) throws IllegalAccessException, InvocationTargetException {
        dbFieldType.assign(object, value);
    }

    public String getColumnName() {
        return dbFieldType.getColumnName() + ID_SUFFIX;
    }

    public static ForeignFieldType build(Field field) throws NoSuchMethodException, NoSuchFieldException {
        ForeignFieldType foreignFieldType = new ForeignFieldType();

        foreignFieldType.dbFieldType = DBFieldType.DBFieldTypeCache.build(field);
        String column = field.getType().getAnnotation(DBTable.class).primaryKey().column();
        foreignFieldType.foreignPrimaryKey = PrimaryKeyFieldType.build(column, field.getType());
        foreignFieldType.foreignFieldClass = field.getDeclaringClass();
        foreignFieldType.dataPersister = foreignFieldType.foreignPrimaryKey.getDbFieldType().getDataPersister();
        foreignFieldType.dataType = foreignFieldType.foreignPrimaryKey.getDbFieldType().getDataType();

        return foreignFieldType;
    }
}
