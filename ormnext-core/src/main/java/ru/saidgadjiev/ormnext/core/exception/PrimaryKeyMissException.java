package ru.saidgadjiev.ormnext.core.exception;

/**
 * Exception will be thrown when impossible resolve primary key for type.
 *
 * @author Said Gadjiev
 */
public class PrimaryKeyMissException extends RuntimeException {

    /**
     * Constructs a new instance with the specified class.
     * @param clazz target class
     */
    public PrimaryKeyMissException(Class<?> clazz) {
        super("Class " + clazz.getName() + " doesn't have primary key");
    }
}
