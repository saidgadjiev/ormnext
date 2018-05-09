package ru.saidgadjiev.ormnext.core.exception;

import java.lang.reflect.Field;

public class DefaultValueParseException extends RuntimeException {

    public DefaultValueParseException(Field field, String defaultValue) {
        super("Parse exception defaultValue " + defaultValue + " for " + field.getDeclaringClass().getName() + " " + field.getName());
    }
}
