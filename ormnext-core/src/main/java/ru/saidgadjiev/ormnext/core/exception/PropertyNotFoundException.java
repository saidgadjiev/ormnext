package ru.saidgadjiev.ormnext.core.exception;

/**
 * Exception will be thrown when property not found in type.
 *
 * @author Said Gadjiev
 */
public class PropertyNotFoundException extends RuntimeException {

    /**
     * Constructs a new instance with the specified class and property name.
     *
     * @param clazz        target class
     * @param propertyName target property name
     */
    public PropertyNotFoundException(Class<?> clazz, String propertyName) {
        super("Property " + clazz.getName() + " " + propertyName + " not exist");
    }
}
