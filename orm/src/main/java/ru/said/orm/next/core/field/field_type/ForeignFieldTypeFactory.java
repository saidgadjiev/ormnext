package ru.said.orm.next.core.field.field_type;

import ru.said.orm.next.core.field.DBField;
import ru.said.orm.next.core.field.persisters.DataPersister;
import ru.said.orm.next.core.table.utils.TableInfoUtils;
import ru.said.up.cache.core.Cache;
import ru.said.up.cache.core.CacheBuilder;

import java.lang.reflect.Field;

/**
 * Created by said on 27.01.2018.
 */
public class ForeignFieldTypeFactory implements FieldTypeFactory {

    private static final Cache<Field, ForeignFieldType> CACHE = CacheBuilder.newRefenceCacheBuilder().build();

    @Override
    public IDBFieldType createFieldType(Field field) throws Exception {
        if (!field.isAnnotationPresent(DBField.class)) {
            return null;
        }
        if (CACHE.contains(field)) {
            return CACHE.get(field);
        }
        IDBFieldType dbFieldType = new DBFieldTypeFactory().createFieldType(field);
        ForeignFieldType foreignFieldType = new ForeignFieldType(dbFieldType);
        IDBFieldType foreignPrimaryKey = TableInfoUtils.resolvePrimaryKey(field.getType()).get();
        DataPersister<?> dataPersister = foreignPrimaryKey.getDataPersister();

        foreignFieldType.setForeignAutoCreate(field.getAnnotation(DBField.class).foreignAutoCreate());
        foreignFieldType.setForeignPrimaryKey(foreignPrimaryKey);
        foreignFieldType.setForeignTableName(TableInfoUtils.resolveTableName(foreignPrimaryKey.getField().getDeclaringClass()));
        foreignFieldType.setForeignFieldClass(field.getType());
        foreignFieldType.setDataPersister(dataPersister);
        foreignFieldType.setDataType(foreignPrimaryKey.getDataType());


        CACHE.put(field, foreignFieldType);

        return foreignFieldType;
    }
}
