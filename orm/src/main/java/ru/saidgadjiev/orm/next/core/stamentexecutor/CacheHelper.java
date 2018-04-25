package ru.saidgadjiev.orm.next.core.stamentexecutor;

import ru.saidgadjiev.orm.next.core.cache.CacheContext;
import ru.saidgadjiev.orm.next.core.cache.ObjectCache;

import java.util.Optional;

public class CacheHelper {

    private CacheContext sessionFactoryCacheContext;

    public CacheHelper(CacheContext sessionFactoryCacheContext) {
        this.sessionFactoryCacheContext = sessionFactoryCacheContext;
    }

    public void saveToCache(Object id, Object object) {
        if (sessionFactoryCacheContext.isCaching(object.getClass())) {
            ObjectCache objectCache = sessionFactoryCacheContext.getObjectCache();

            objectCache.put(object.getClass(), id, object);
        }
    }

    public void delete(Class<?> objectClass, Object id) {
        if (sessionFactoryCacheContext.isCaching(objectClass)) {
            sessionFactoryCacheContext.getObjectCache().invalidate(objectClass, id);
        }
    }

    public Optional<Object> get(Class<?> objectClass, Object id) {
        if (sessionFactoryCacheContext.isCaching(objectClass)) {
            ObjectCache objectCache = sessionFactoryCacheContext.getObjectCache();

            return Optional.ofNullable(objectCache.get(objectClass, id));
        }

        return Optional.empty();
    }
}
