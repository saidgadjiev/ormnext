package ru.saidgadjiev.ormnext.core.validator.entity;

import ru.saidgadjiev.ormnext.core.exception.ForeignKeyIsNotDefinedException;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;
import ru.saidgadjiev.ormnext.core.utils.DatabaseEntityMetadataUtils;

import java.lang.reflect.Field;

public class ForeignKeyValidator implements IValidator {

    public <T> void validate(Class<T> tClass) {
        for (Field field : tClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ForeignColumn.class) && !DatabaseEntityMetadataUtils.resolvePrimaryKey(field.getType()).isPresent()) {
                throw new ForeignKeyIsNotDefinedException(field);
            }
        }
    }
}
