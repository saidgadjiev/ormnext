package ru.saidgadjiev.orm.next.core.table.internal.instatiator;

import ru.saidgadjiev.orm.next.core.exception.InstantiationException;

import java.lang.reflect.Constructor;
import java.util.Optional;

public class EntityInstantiator implements Instantiator {

    private Class<?> entityClass;

    private Constructor<?> constructor;

    public EntityInstantiator(Class<?> entityClass) {
        this.entityClass = entityClass;
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
