package ru.saidgadjiev.ormnext.core.validator.entity;

import ru.saidgadjiev.ormnext.core.exception.PrimaryKeyMissException;
import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;

import java.lang.reflect.Field;

/**
 * Check is defined primary key or not.
 * If not throw {@link PrimaryKeyMissException}.
 *
 * @author said gadjiev
 */
public class PrimaryKeyValidator implements Validator {

    @Override
    public<T> void validate(Class<T> entityClass) {
        int idCount = 0;

        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(DatabaseColumn.class)) {
                DatabaseColumn databaseColumn = field.getAnnotation(DatabaseColumn.class);

                if (databaseColumn.id()) {
                    ++idCount;
                }
            }
        }
        if (idCount == 0) {
            throw new PrimaryKeyMissException(entityClass);
        }
    }
}
