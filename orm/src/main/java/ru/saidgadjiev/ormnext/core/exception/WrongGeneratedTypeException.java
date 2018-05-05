package ru.saidgadjiev.ormnext.core.exception;

import java.lang.reflect.Field;

public class WrongGeneratedTypeException extends RuntimeException {

    public WrongGeneratedTypeException(Field field, String availableTypes) {
        super("Field " + field.getDeclaringClass().getName() + " " + field.getName() + " must be in " + availableTypes);
    }
}
