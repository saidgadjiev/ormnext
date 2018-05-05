package ru.saidgadjiev.ormnext.core.exception;

public class DefaultConstructorNotFoundException extends RuntimeException {

    public DefaultConstructorNotFoundException(Class<?> tClass) {
        super("Class " + tClass.getName() + " have not default constructor");
    }
}
