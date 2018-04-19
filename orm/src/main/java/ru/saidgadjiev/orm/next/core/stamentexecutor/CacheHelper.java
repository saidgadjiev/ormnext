package ru.saidgadjiev.orm.next.core.stamentexecutor;

import ru.saidgadjiev.orm.next.core.cache.CacheContext;
import ru.saidgadjiev.orm.next.core.cache.ObjectCache;

import java.sql.SQLException;
import java.util.Optional;

public class CacheHelper {

    private CacheContext sessionFactoryCacheContext;

    private CacheContext sessionCacheContext;

    public CacheHelper(CacheContext sessionFactoryCacheContext, CacheContext sessionCacheContext) {
        this.sessionFactoryCacheContext = sessionFactoryCacheContext;
        this.sessionCacheContext = sessionCacheContext;
    }

    public void saveToCache(Object object, Object id) throws SQLException {
        if (sessionFactoryCacheContext.isCaching(object.getClass())) {
            ObjectCache objectCache = sessionFactoryCacheContext.getObjectCache();

            objectCache.put(object.getClass(), id, object);
        }
        sessionCacheContext.getObjectCache().put(object.getClass(), id, object);
    }

    public void delete(Class<?> objectClass, Object id) throws SQLException {
        if (sessionFactoryCacheContext.isCaching(objectClass)) {
            sessionFactoryCacheContext.getObjectCache().invalidate(objectClass, id);
        }
        sessionCacheContext.getObjectCache().invalidate(objectClass, id);
    }

    public Optional<Object> get(Class<?> objectClass, Object id) {
        if (sessionCacheContext.getObjectCache().contains(objectClass, id)) {
            return Optional.of(sessionCacheContext.getObjectCache().get(objectClass, id));
        }
        if (sessionFactoryCacheContext.isCaching(objectClass)) {
            ObjectCache objectCache = sessionFactoryCacheContext.getObjectCache();

            return Optional.ofNullable(objectCache.get(objectClass, id));
        }

        return Optional.empty();
    }
}
