package ru.saidgadjiev.ormnext.core.validator.entity;

import ru.saidgadjiev.ormnext.core.exception.DefaultConstructorNotFoundException;

import java.lang.reflect.Constructor;

/**
 * Check entity class has default constructor or not.
 * If not throw exception {@link DefaultConstructorNotFoundException}.
 */
public class HasDefaultConstructorValidator implements IValidator {

    @Override
    public<T> void validate(Class<T> entityClass) {
        boolean hasDefaultConstructor = false;

        for (Constructor<?> constructor : entityClass.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                hasDefaultConstructor = true;
            }
        }
        if (!hasDefaultConstructor) {
            throw new DefaultConstructorNotFoundException(entityClass);
        }
    }
}
