package ru.saidgadjiev.ormnext.core.validator.entity;

import ru.saidgadjiev.ormnext.core.exception.PrimaryKeyMissException;
import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;

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
            throw new PrimaryKeyMissException(tClass);
        }
    }
}
