package ru.said.orm.next.core.table.validators;

import java.lang.reflect.Constructor;

public class HasConstructorValidator implements IValidator {

    public<T> void validate(Class<T> tClass) throws IllegalAccessException {
        boolean hasDefaultConstructor = false;

        for (Constructor<?> constructor : tClass.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                hasDefaultConstructor = true;
            }
        }
        if (!hasDefaultConstructor) {
            throw new IllegalAccessException("Class " + tClass.getPackage() + "." + tClass.getSimpleName() + " has no default constructor");
        }
    }
}
