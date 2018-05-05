package ru.saidgadjiev.ormnext.core.validator.entity;

import ru.saidgadjiev.ormnext.core.exception.DefaultConstructorNotFoundException;

import java.lang.reflect.Constructor;

public class HasConstructorValidator implements IValidator {

    public<T> void validate(Class<T> tClass) {
        boolean hasDefaultConstructor = false;

        for (Constructor<?> constructor : tClass.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                hasDefaultConstructor = true;
            }
        }
        if (!hasDefaultConstructor) {
            throw new DefaultConstructorNotFoundException(tClass);
        }
    }
}
