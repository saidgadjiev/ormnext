package ru.saidgadjiev.ormnext.core.exception;

import java.lang.reflect.Field;

public class ForeignKeyIsNotDefinedException extends RuntimeException {

    public ForeignKeyIsNotDefinedException(Field field) {
        super("Foreign key is not defined for " + field.getDeclaringClass().getName() + " " + field.getName());
    }
}
