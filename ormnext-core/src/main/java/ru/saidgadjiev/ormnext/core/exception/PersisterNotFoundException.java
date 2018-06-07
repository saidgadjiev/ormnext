package ru.saidgadjiev.ormnext.core.exception;

/**
 * Exception will be thrown when impossible resolve persister for type.
 *
 * @author Said Gadjiev
 */
public class PersisterNotFoundException extends RuntimeException {

    /**
     * Constructs a new instance with the specified class.
     * @param clazz target class
     */
    public PersisterNotFoundException(Class<?> clazz) {
        super("Persister not found for class " + clazz.getName());
    }

    /**
     * Constructs a new instance with the specified type.
     * @param type target type
     */
    public PersisterNotFoundException(int type) {
        super("Persister not found for type " + type);
    }
}
