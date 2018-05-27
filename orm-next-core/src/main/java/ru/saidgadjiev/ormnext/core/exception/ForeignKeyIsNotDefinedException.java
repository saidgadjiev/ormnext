package ru.saidgadjiev.ormnext.core.exception;

import java.lang.reflect.Field;

/**
 * Exception will be thrown if foreign key is not defined.
 */
public class ForeignKeyIsNotDefinedException extends RuntimeException {

    /**
     * Constructs a new instance with the specified field.
     * @param field target field
     */
    public ForeignKeyIsNotDefinedException(Field field) {
        super("Foreign key is not defined for " + field.getDeclaringClass().getName() + " " + field.getName());
    }
}
