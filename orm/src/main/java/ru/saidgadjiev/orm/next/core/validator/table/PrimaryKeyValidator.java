package ru.saidgadjiev.orm.next.core.validator.table;

import ru.saidgadjiev.orm.next.core.field.DatabaseColumn;
import ru.saidgadjiev.orm.next.core.utils.ExceptionUtils;

import java.lang.reflect.Field;

public class PrimaryKeyValidator implements IValidator {

    public<T> void validate(Class<T> tClass) {
        int idCount = 0;

        for (Field field : tClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(DatabaseColumn.class)) {
                DatabaseColumn databaseColumn = field.getAnnotation(DatabaseColumn.class);

                if (databaseColumn.id()) {
                    ++idCount;
                }
            }
        }
        if (idCount == 0) {
            throw new IllegalArgumentException(ExceptionUtils.message(ExceptionUtils.Exception.PRIMARY_KEY_MISS, tClass));
        }
    }
}
