package ru.saidgadjiev.orm.next.core.utils;

import java.lang.reflect.Constructor;
import java.util.Optional;

public class ReflectUtils {

    private ReflectUtils() {

    }

    public static Optional<Constructor<?>> lookupDefaultConstructor(Class<?> clazz) {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                return Optional.of(constructor);
            }
        }

        return Optional.empty();
    }
}
