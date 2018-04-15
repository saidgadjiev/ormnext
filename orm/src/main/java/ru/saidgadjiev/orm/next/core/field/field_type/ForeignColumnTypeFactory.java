package ru.saidgadjiev.orm.next.core.field.field_type;

import ru.saidgadjiev.orm.next.core.field.FieldAccessor;
import ru.saidgadjiev.orm.next.core.field.ForeignColumn;
import ru.saidgadjiev.orm.next.core.field.persister.DataPersister;
import ru.saidgadjiev.orm.next.core.utils.TableInfoUtils;
import ru.saidgadjiev.orm.next.core.validator.data_persister.DataTypeValidator;
import ru.saidgadjiev.orm.next.core.validator.data_persister.GeneratedTypeValidator;
import ru.saidgadjiev.orm.next.core.validator.table.PrimaryKeyValidator;

import java.lang.reflect.Field;

/**
 * Created by said on 27.01.2018.
 */
public class ForeignColumnTypeFactory implements ColumnTypeFactory {

    @Override
    public IDatabaseColumnType createFieldType(Field field) throws IllegalArgumentException {
        if (!field.isAnnotationPresent(ForeignColumn.class)) {
            return null;
        }
        ForeignColumn foreignColumn = field.getAnnotation(ForeignColumn.class);
        new PrimaryKeyValidator().validate(field.getType());
        ForeignColumnype foreignColumnype = new ForeignColumnype();
        IDatabaseColumnType foreignPrimaryKey = TableInfoUtils.resolvePrimaryKey(field.getType()).get();
        DataPersister<?> dataPersister = foreignPrimaryKey.getDataPersister();

        new DataTypeValidator(foreignPrimaryKey.getField()).validate(dataPersister);
        new GeneratedTypeValidator(foreignPrimaryKey.isGenerated()).validate(dataPersister);
        foreignColumnype.setForeignAutoCreate(foreignColumn.foreignAutoCreate());
        foreignColumnype.setForeignPrimaryKey(foreignPrimaryKey);
        foreignColumnype.setForeignTableName(TableInfoUtils.resolveTableName(foreignPrimaryKey.getField().getDeclaringClass()));
        foreignColumnype.setForeignFieldClass(field.getType());
        foreignColumnype.setDataPersister(dataPersister);
        foreignColumnype.setDataType(foreignPrimaryKey.getDataType());
        foreignColumnype.setField(field);
        try {
            foreignColumnype.setFieldAccessor(new FieldAccessor(field));
        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException(ex);
        }
        foreignColumnype.setColumnName(foreignColumn.columnName());

        return foreignColumnype;
    }
}
