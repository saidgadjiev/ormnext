package ru.saidgadjiev.ormnext.core.exception;

import java.lang.reflect.Field;

/**
 * Exception will be thrown if generated value not found in result set.
 */
public class GeneratedValueNotFoundException extends RuntimeException {

    /**
     * Constructs a new instance with the specified field.
     * @param field target field
     */
    public GeneratedValueNotFoundException(Field field) {
        super("Generated value not found for " + field.getDeclaringClass().getName() + " " + field.getName());
    }
}
