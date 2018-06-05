package ru.saidgadjiev.ormnext.core.table.internal.instatiator;

import ru.saidgadjiev.ormnext.core.exception.InstantiationException;

import java.lang.reflect.Constructor;
import java.util.Optional;

/**
 * Object instantiator by default constructor.
 *
 * @author said gadjiev
 */
public class ObjectInstantiator implements Instantiator {

    /**
     * Object class.
     */
    private Class<?> objectClass;

    /**
     * Default constructor.
     */
    private Constructor<?> constructor;

    /**
     * Create a new instantiator instance.
     * @param objectClass target object class
     */
    public ObjectInstantiator(Class<?> objectClass) {
        this.objectClass = objectClass;
    }

    @Override
    public synchronized Object instantiate() {
        if (constructor == null) {
            constructor = lookupDefaultConstructor(objectClass)
                    .orElseThrow(() -> new InstantiationException("No default constructor for entity: ", objectClass));
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
            throw new InstantiationException("Could not instantiate entity: ", objectClass, ex);
        }
    }

    /**
     * Resolve default constructor.
     * @param clazz target class
     * @return resolved default constructor
     */
    private static Optional<Constructor<?>> lookupDefaultConstructor(Class<?> clazz) {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                return Optional.of(constructor);
            }
        }

        return Optional.empty();
    }
}
