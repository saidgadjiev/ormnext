package ru.saidgadjiev.ormnext.core.exception;

import java.lang.reflect.Field;

public class GeneratedValueNotFoundException extends RuntimeException {

    public GeneratedValueNotFoundException(Field field) {
        super("Generated value not found for " + field.getDeclaringClass().getName() + " " + field.getName());
    }
}
