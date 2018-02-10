package ru.saidgadjiev.orm.next.core.field.field_type;

import ru.saidgadjiev.orm.next.core.field.DBField;
import ru.saidgadjiev.orm.next.core.field.DataPersisterManager;
import ru.saidgadjiev.orm.next.core.field.DataType;
import ru.saidgadjiev.orm.next.core.field.FieldAccessor;
import ru.saidgadjiev.orm.next.core.field.persisters.DataPersister;
import ru.saidgadjiev.orm.next.core.validator.data_persister.DataTypeValidator;
import ru.saidgadjiev.orm.next.core.validator.data_persister.GeneratedTypeValidator;

import java.lang.reflect.Field;

/**
 * Created by said on 27.01.2018.
 */
public class DBFieldTypeFactory implements FieldTypeFactory {

    @Override
    public IDBFieldType createFieldType(Field field) throws Exception {
        if (!field.isAnnotationPresent(DBField.class)) {
            return null;
        }
        DBField dbField = field.getAnnotation(DBField.class);
        DBFieldType fieldType = new DBFieldType();

        fieldType.setField(field);
        fieldType.setColumnName(dbField.columnName().isEmpty() ? field.getName().toLowerCase() : dbField.columnName());
        fieldType.setLength(dbField.length());
        fieldType.setFieldAccessor(new FieldAccessor(field));
        String format = dbField.format();

        fieldType.setFormat(format);
        if (!dbField.foreign()) {
            DataType dataType = dbField.dataType();
            DataPersister<?> dataPersister = dataType.equals(DataType.UNKNOWN) ? DataPersisterManager.lookup(field.getType()) : dataType.getDataPersister();


            new DataTypeValidator(field).validate(dataPersister);
            new GeneratedTypeValidator(dbField.generated()).validate(dataPersister);
            fieldType.setDataPersister(dataPersister);
            fieldType.setDataType(dataPersister.getDataType());
            String defaultValue = dbField.defaultValue();

            if (!defaultValue.equals(DBField.DEFAULT_STR)) {
                fieldType.setDefaultValue(dataPersister.parseDefaultTo(fieldType, dbField.defaultValue()));
            }
        }
        fieldType.setNotNull(dbField.notNull());
        fieldType.setId(dbField.id());
        fieldType.setGenerated(dbField.generated());

        return fieldType;
    }
}
