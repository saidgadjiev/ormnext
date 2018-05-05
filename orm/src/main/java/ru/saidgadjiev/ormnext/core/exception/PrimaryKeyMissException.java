package ru.saidgadjiev.ormnext.core.exception;

public class PrimaryKeyMissException extends RuntimeException {

    public PrimaryKeyMissException(Class<?> entityClass) {
        super("Class " + entityClass.getName() + " doesn't have primary key");
    }
}
