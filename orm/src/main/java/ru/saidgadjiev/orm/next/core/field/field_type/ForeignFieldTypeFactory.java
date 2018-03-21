package ru.saidgadjiev.orm.next.core.field.field_type;

import ru.saidgadjiev.orm.next.core.field.DBField;
import ru.saidgadjiev.orm.next.core.field.persisters.DataPersister;
import ru.saidgadjiev.orm.next.core.utils.TableInfoUtils;
import ru.saidgadjiev.orm.next.core.validator.data_persister.DataTypeValidator;
import ru.saidgadjiev.orm.next.core.validator.data_persister.GeneratedTypeValidator;
import ru.saidgadjiev.orm.next.core.validator.table.PrimaryKeyValidator;

import java.lang.reflect.Field;

/**
 * Created by said on 27.01.2018.
 */
public class ForeignFieldTypeFactory implements FieldTypeFactory {

    @Override
    public IDBFieldType createFieldType(Field field) throws IllegalArgumentException {
        if (!field.isAnnotationPresent(DBField.class)) {
            return null;
        }
        new PrimaryKeyValidator().validate(field.getType());
        IDBFieldType dbFieldType = new DBFieldTypeFactory().createFieldType(field);
        ForeignFieldType foreignFieldType = new ForeignFieldType(dbFieldType);
        IDBFieldType foreignPrimaryKey = TableInfoUtils.resolvePrimaryKey(field.getType()).get();
        DataPersister<?> dataPersister = foreignPrimaryKey.getDataPersister();

        new DataTypeValidator(foreignPrimaryKey.getField()).validate(dataPersister);
        new GeneratedTypeValidator(foreignPrimaryKey.isGenerated()).validate(dataPersister);
        foreignFieldType.setForeignAutoCreate(field.getAnnotation(DBField.class).foreignAutoCreate());
        foreignFieldType.setForeignPrimaryKey(foreignPrimaryKey);
        foreignFieldType.setForeignTableName(TableInfoUtils.resolveTableName(foreignPrimaryKey.getField().getDeclaringClass()));
        foreignFieldType.setForeignFieldClass(field.getType());
        foreignFieldType.setDataPersister(dataPersister);
        foreignFieldType.setDataType(foreignPrimaryKey.getDataType());

        return foreignFieldType;
    }
}
