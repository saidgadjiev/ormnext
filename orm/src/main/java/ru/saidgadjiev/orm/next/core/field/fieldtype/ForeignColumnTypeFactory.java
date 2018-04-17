package ru.saidgadjiev.orm.next.core.field.fieldtype;

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
        ForeignColumnType foreignColumnType = new ForeignColumnType();
        IDatabaseColumnType foreignPrimaryKey = TableInfoUtils.resolvePrimaryKey(field.getType()).get();
        DataPersister<?> dataPersister = foreignPrimaryKey.getDataPersister();

        new DataTypeValidator(foreignPrimaryKey.getField()).validate(dataPersister);
        new GeneratedTypeValidator(foreignPrimaryKey.isGenerated()).validate(dataPersister);
        foreignColumnType.setForeignAutoCreate(foreignColumn.foreignAutoCreate());
        foreignColumnType.setForeignPrimaryKey(foreignPrimaryKey);
        foreignColumnType.setForeignTableName(TableInfoUtils.resolveTableName(foreignPrimaryKey.getField().getDeclaringClass()));
        foreignColumnType.setForeignFieldClass(field.getType());
        foreignColumnType.setOwnerClass(field.getDeclaringClass());
        foreignColumnType.setDataPersister(dataPersister);
        foreignColumnType.setDataType(foreignPrimaryKey.getDataType());
        foreignColumnType.setField(field);
        try {
            foreignColumnType.setFieldAccessor(new FieldAccessor(field));
        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException(ex);
        }
        foreignColumnType.setColumnName(foreignColumn.columnName().isEmpty() ? field.getName().toLowerCase() : foreignColumn.columnName());

        return foreignColumnType;
    }
}
