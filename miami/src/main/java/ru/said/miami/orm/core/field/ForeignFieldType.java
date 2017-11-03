package ru.said.miami.orm.core.field;

import ru.said.miami.orm.core.field.persisters.DataPersister;
import ru.said.miami.orm.core.table.TableInfoUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by said on 30.10.17.
 */
public class ForeignFieldType {

    private static final String ID_SUFFIX = "_id";

    private DBFieldType dbFieldType;

    private DBFieldType foreignPrimaryKey;

    private boolean foreignAutoCreate;

    private Class<?> foreignFieldClass;

    private DataPersister dataPersister;

    private String foreignTableName;

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

    public DBFieldType getForeignPrimaryKey() {
        return foreignPrimaryKey;
    }

    public Object access(Object object) throws InvocationTargetException, IllegalAccessException {
        return foreignPrimaryKey.access(object);
    }

    public void assign(Object object, Object value) throws IllegalAccessException, InvocationTargetException {
        foreignPrimaryKey.assign(object, value);
    }

    public String getColumnName() {
        return dbFieldType.getColumnName() + ID_SUFFIX;
    }

    public String getForeignTableName() {
        return foreignTableName;
    }

    public String getForeignColumnName() {
        return foreignPrimaryKey.getColumnName();
    }

    public static ForeignFieldType build(Field field) throws NoSuchMethodException, NoSuchFieldException {
        ForeignFieldType foreignFieldType = new ForeignFieldType();

        foreignFieldType.dbFieldType = DBFieldType.build(field);
        foreignFieldType.foreignPrimaryKey = TableInfoUtils.resolvePrimaryKey(field.getType()).get();
        foreignFieldType.foreignTableName = TableInfoUtils.resolveTableName(foreignFieldType.foreignPrimaryKey.getField().getDeclaringClass());
        foreignFieldType.foreignFieldClass = field.getDeclaringClass();
        foreignFieldType.dataPersister = foreignFieldType.foreignPrimaryKey.getDataPersister();
        foreignFieldType.dataType = foreignFieldType.foreignPrimaryKey.getDataType();

        return foreignFieldType;
    }
}
