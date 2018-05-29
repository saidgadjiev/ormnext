package ru.saidgadjiev.ormnext.core.exception;

/**
 * Exception will be thrown when found unregistered entity class.
 *
 * @author said gadjiev
 */
public class NotRegisteredEntityFoundException extends RuntimeException {

    /**
     * Constructs a new instance with the specified class.
     * @param entityClass target class
     */
    public NotRegisteredEntityFoundException(Class<?> entityClass) {
        super("Entity " + entityClass + " not registered");
    }
}
