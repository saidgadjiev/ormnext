package ru.saidgadjiev.orm.next.core.validator.table;

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
            throw new IllegalArgumentException("Class " + tClass.getPackage() + "." + tClass.getSimpleName() + " has no default constructor");
        }
    }
}
