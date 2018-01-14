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
public class ForeignFieldType implements IDBFieldType {

    private static final String ID_SUFFIX = "_id";

    private DBFieldType dbFieldType;

    private DBFieldType foreignPrimaryKey;

    private boolean foreignAutoCreate;

    private Class<?> foreignFieldClass;

    private DataPersister dataPersister;

    private String foreignTableName;

    private DataType dataType;

    @Override
    public boolean isId() {
        return dbFieldType.isId();
    }

    @Override
    public boolean isNotNull() {
        return dbFieldType.isNotNull();
    }

    @Override
    public boolean isGenerated() {
        return dbFieldType.isGenerated();
    }

    @Override
    public Field getField() {
        return dbFieldType.getField();
    }

    @Override
    public int getLength() {
        return dbFieldType.getLength();
    }

    public boolean isForeignAutoCreate() {
        return foreignAutoCreate;
    }

    public Class<?> getForeignFieldClass() {
        return foreignFieldClass;
    }

    @Override
    public DataPersister getDataPersister() {
        return dataPersister;
    }

    @Override
    public DataType getDataType() {
        return dataType;
    }

    public DBFieldType getForeignPrimaryKey() {
        return foreignPrimaryKey;
    }

    @Override
    public Object access(Object object) throws InvocationTargetException, IllegalAccessException {
        return dbFieldType.access(object);
    }

    @Override
    public void assign(Object object, Object value) throws IllegalAccessException, InvocationTargetException {
        dbFieldType.assign(object, value);
    }

    @Override
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
