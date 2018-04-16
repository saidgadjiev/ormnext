package ru.saidgadjiev.orm.next.core.table.persister.instatiator;

import ru.saidgadjiev.orm.next.core.exception.InstantiationException;
import ru.saidgadjiev.orm.next.core.utils.ReflectUtils;

import java.lang.reflect.Constructor;

public class EntityInstantiator implements Instantiator {

    private Class<?> entityClass;

    private Constructor<?> constructor;

    public EntityInstantiator(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public synchronized Object instantiate() {
        if (constructor == null) {
            constructor = ReflectUtils.lookupDefaultConstructor(entityClass)
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
}
