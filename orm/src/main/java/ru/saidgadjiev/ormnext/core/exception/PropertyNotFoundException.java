package ru.saidgadjiev.ormnext.core.exception;

public class PropertyNotFoundException extends RuntimeException {

    public PropertyNotFoundException(Class<?> clazz, String propertyName) {
        super("Property " + clazz.getName() + " " + propertyName + " not exist");
    }
}
