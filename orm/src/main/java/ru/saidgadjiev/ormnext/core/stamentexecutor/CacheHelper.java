package ru.saidgadjiev.ormnext.core.stamentexecutor;

import ru.saidgadjiev.ormnext.core.cache.CacheAccess;
import ru.saidgadjiev.ormnext.core.cache.ObjectCache;

import java.util.Optional;

public class CacheHelper {

    private CacheAccess sessionFactoryCacheAccess;

    public CacheHelper(CacheAccess sessionFactoryCacheAccess) {
        this.sessionFactoryCacheAccess = sessionFactoryCacheAccess;
    }

    public void saveToCache(Object id, Object object) {
        if (sessionFactoryCacheAccess.isCaching(object.getClass())) {
            ObjectCache objectCache = sessionFactoryCacheAccess.getObjectCache();

            objectCache.put(object.getClass(), id, object);
        }
    }

    public void delete(Class<?> objectClass, Object id) {
        if (sessionFactoryCacheAccess.isCaching(objectClass)) {
            sessionFactoryCacheAccess.getObjectCache().invalidate(objectClass, id);
        }
    }

    public Optional<Object> get(Class<?> objectClass, Object id) {
        if (sessionFactoryCacheAccess.isCaching(objectClass)) {
            ObjectCache objectCache = sessionFactoryCacheAccess.getObjectCache();

            return Optional.ofNullable(objectCache.get(objectClass, id));
        }

        return Optional.empty();
    }
}
