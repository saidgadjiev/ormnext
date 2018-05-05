package ru.saidgadjiev.ormnext.core.exception;

public class PersisterNotFoundException extends RuntimeException {

    public PersisterNotFoundException(Class<?> persistedClass) {
        super("Persister not found for class " + persistedClass.getName());
    }

    public PersisterNotFoundException(int type) {
        super("Persister not found for type " + type);
    }
}
