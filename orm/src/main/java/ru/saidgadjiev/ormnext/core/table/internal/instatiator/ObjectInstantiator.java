package ru.saidgadjiev.ormnext.core.table.internal.instatiator;

import ru.saidgadjiev.ormnext.core.exception.InstantiationException;

import java.lang.reflect.Constructor;
import java.util.Optional;

public class ObjectInstantiator implements Instantiator {

    private Class<?> entityClass;

    private Constructor<?> constructor;

    public ObjectInstantiator(Class<?> objectClass) {
        this.entityClass = objectClass;
    }

    @Override
    public synchronized Object instantiate() {
        if (constructor == null) {
            constructor = lookupDefaultConstructor(entityClass)
                    .orElseThrow(() -> new InstantiationException("No default constructor for entity: ", entityClass));
        }
        Object object;

        try {
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
                object = constructor.newInstance();
                constructor.setAccessible(false);
            } else {
                object = constructor.newInstance();
            }

            return object;
        } catch (Exception ex) {
            throw new InstantiationException( "Could not instantiate entity: ", entityClass, ex);
        }
    }

    private static Optional<Constructor<?>> lookupDefaultConstructor(Class<?> clazz) {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                return Optional.of(constructor);
            }
        }

        return Optional.empty();
    }
}
