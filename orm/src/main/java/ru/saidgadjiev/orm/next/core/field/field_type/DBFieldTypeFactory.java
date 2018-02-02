package ru.saidgadjiev.orm.next.core.field.field_type;

import ru.saidgadjiev.orm.next.core.field.DBField;
import ru.saidgadjiev.orm.next.core.field.DataPersisterManager;
import ru.saidgadjiev.orm.next.core.field.DataType;
import ru.saidgadjiev.orm.next.core.field.FieldAccessor;
import ru.saidgadjiev.orm.next.core.field.persisters.DataPersister;
import ru.saidgadjiev.up.cache.core.Cache;
import ru.saidgadjiev.up.cache.core.CacheBuilder;

import java.lang.reflect.Field;

/**
 * Created by said on 27.01.2018.
 */
public class DBFieldTypeFactory implements FieldTypeFactory {

    private static final Cache<Field, DBFieldType> CACHE = CacheBuilder.newRefenceCacheBuilder().build();

    @Override
    public IDBFieldType createFieldType(Field field) throws Exception {
        if (!field.isAnnotationPresent(DBField.class)) {
            return null;
        }
        if (CACHE.contains(field)) {
            return CACHE.get(field);
        }
        DBField dbField = field.getAnnotation(DBField.class);
        DBFieldType fieldType = new DBFieldType();

        fieldType.setField(field);
        fieldType.setColumnName(dbField.columnName().isEmpty() ? field.getName().toLowerCase() : dbField.columnName());
        fieldType.setLength(dbField.length());
        fieldType.setFieldAccessor(new FieldAccessor(field));
        if (!dbField.foreign()) {
            DataType dataType = dbField.dataType();
            DataPersister<?> dataPersister = dataType.equals(DataType.UNKNOWN) ? DataPersisterManager.lookup(field.getType()) : dataType.getDataPersister();

            fieldType.setDataPersister(dataPersister);
            fieldType.setDataType(dataPersister.getDataType());
            fieldType.setFieldConverter(dataPersister);
            String defaultValue = dbField.defaultValue();

            if (!defaultValue.equals(DBField.DEFAULT_STR)) {
                fieldType.setDefaultValue(dataPersister.convertTo(dbField.defaultValue()));
            }
        }
        fieldType.setNotNull(dbField.notNull());
        fieldType.setId(dbField.id());
        fieldType.setGenerated(dbField.generated());

        CACHE.put(field, fieldType);

        return fieldType;
    }
}
