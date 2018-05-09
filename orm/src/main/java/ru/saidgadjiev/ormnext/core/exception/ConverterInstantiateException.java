package ru.saidgadjiev.ormnext.core.exception;

public class ConverterInstantiateException extends RuntimeException {

    public ConverterInstantiateException(Class<?> converterClass, Throwable clause) {
        super("Converter " + converterClass + " instantiate exception. " + clause.getMessage(), clause);
    }
}
