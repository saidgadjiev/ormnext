package ru.saidgadjiev.orm.next.core.validator.table;

import ru.saidgadjiev.orm.next.core.field.DBField;
import ru.saidgadjiev.orm.next.core.utils.ExceptionUtils;

import java.lang.reflect.Field;

public class PrimaryKeyValidator implements IValidator {

    public<T> void validate(Class<T> tClass) throws IllegalAccessException {
        int idCount = 0;

        for (Field field : tClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(DBField.class)) {
                DBField dbField = field.getAnnotation(DBField.class);

                if (dbField.id()) {
                    ++idCount;
                }
            }
        }
        if (idCount == 0) {
            throw new IllegalArgumentException(ExceptionUtils.message(ExceptionUtils.Exception.PRIMARY_KEY_MISS, tClass));
        }
    }
}
