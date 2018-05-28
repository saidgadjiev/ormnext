package ru.saidgadjiev.ormnext.core.exception;

/**
 * Exception will be thrown if class does not have default constructor.
 *
 * @author said gadjiev
 */
public class DefaultConstructorNotFoundException extends RuntimeException {

    /**
     * Constructs a new instance with the class.
     *
     * @param clazz target class
     */
    public DefaultConstructorNotFoundException(Class<?> clazz) {
        super("Class " + clazz.getName() + " have not default constructor");
    }
}
