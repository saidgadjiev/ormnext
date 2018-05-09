package ru.saidgadjiev.ormnext.core.exception;

public class UnknownTypeException extends RuntimeException {

    public UnknownTypeException(int type) {
        super("Type " + type + " unknown");
    }
}
