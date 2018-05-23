package ru.saidgadjiev.ormnext.core.exception;

public class NotRegisteredEntityFoundException extends RuntimeException {

    public NotRegisteredEntityFoundException(Class<?> entityClass) {
        super("Entity " + entityClass + " not registered");
    }
}
