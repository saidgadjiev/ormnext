package ru.said.miami.orm.core.field.fieldTypes;

import ru.said.miami.cache.core.Cache;
import ru.said.miami.cache.core.CacheBuilder;
import ru.said.miami.orm.core.field.DBField;
import ru.said.miami.orm.core.field.DataType;
import ru.said.miami.orm.core.field.persisters.DataPersister;
import ru.said.miami.orm.core.table.utils.TableInfoUtils;

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
        return dbFieldType.access(object);
    }

    public void assign(Object object, Object value) throws IllegalAccessException, InvocationTargetException {
        dbFieldType.assign(object, value);
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

        foreignFieldType.dbFieldType = DBFieldType.DBFieldTypeCache.build(field);
        foreignFieldType.foreignAutoCreate = field.getAnnotation(DBField.class).foreignAutoCreate();
        foreignFieldType.foreignPrimaryKey = TableInfoUtils.resolvePrimaryKey(field.getType()).get();
        foreignFieldType.foreignTableName = TableInfoUtils.resolveTableName(foreignFieldType.foreignPrimaryKey.getField().getDeclaringClass());
        foreignFieldType.foreignFieldClass = field.getType();
        foreignFieldType.dataPersister = foreignFieldType.foreignPrimaryKey.getDataPersister();
        foreignFieldType.dataType = foreignFieldType.foreignPrimaryKey.getDataType();

        return foreignFieldType;
    }

    public static class ForeignFieldTypeCache {

        private static final Cache<Field, ForeignFieldType> CACHE = CacheBuilder.newRefenceCacheBuilder().build();

        public static ForeignFieldType build(Field field) throws NoSuchMethodException, NoSuchFieldException {
            if (CACHE.contains(field)) {
                return CACHE.get(field);
            }
            ForeignFieldType foreignFieldType = ForeignFieldType.build(field);

            CACHE.put(field, foreignFieldType);

            return foreignFieldType;
        }
    }
}
