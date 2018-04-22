package ru.saidgadjiev.orm.next.core.field.fieldtype;

import ru.saidgadjiev.orm.next.core.field.*;
import ru.saidgadjiev.orm.next.core.field.persister.DataPersister;
import ru.saidgadjiev.orm.next.core.validator.datapersister.DataTypeValidator;
import ru.saidgadjiev.orm.next.core.validator.datapersister.GeneratedTypeValidator;

import java.lang.reflect.Field;

/**
 * Created by said on 27.01.2018.
 */
public class DatabaseColumnTypeFactory implements ColumnTypeFactory {

    @Override
    public IDatabaseColumnType createFieldType(Field field) throws IllegalArgumentException {
        if (!field.isAnnotationPresent(DatabaseColumn.class)) {
            return null;
        }
        DatabaseColumn databaseColumn = field.getAnnotation(DatabaseColumn.class);
        DatabaseColumnType fieldType = new DatabaseColumnType();

        fieldType.setField(field);
        fieldType.setColumnName(databaseColumn.columnName().isEmpty() ? field.getName().toLowerCase() : databaseColumn.columnName());
        fieldType.setLength(databaseColumn.length());
        try {
            fieldType.setFieldAccessor(new FieldAccessor(field));
        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException(ex);
        }
        String format = databaseColumn.format();

        fieldType.setFormat(format);
        if (!field.isAnnotationPresent(ForeignColumn.class)) {
            Integer dataType = databaseColumn.dataType();
            DataPersister<?> dataPersister = dataType.equals(DataType.UNKNOWN) ? DataPersisterManager.lookup(field.getType()) : DataPersisterManager.lookup(dataType);

            new DataTypeValidator(field).validate(dataPersister);
            new GeneratedTypeValidator(databaseColumn.generated()).validate(dataPersister);
            fieldType.setDataPersister(dataPersister);
            fieldType.setDataType(dataType.equals(DataType.UNKNOWN) ? dataPersister.getDataType() : dataType);
            String defaultValue = databaseColumn.defaultValue();

            if (!defaultValue.equals(DatabaseColumn.DEFAULT_STR)) {
                fieldType.setDefaultValue(dataPersister.parseDefaultTo(fieldType, databaseColumn.defaultValue()));
            }
        }
        fieldType.setNotNull(databaseColumn.notNull());
        fieldType.setId(databaseColumn.id());
        fieldType.setGenerated(databaseColumn.generated());

        return fieldType;
    }
}
