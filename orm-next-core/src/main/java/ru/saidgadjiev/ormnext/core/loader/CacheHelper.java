package ru.saidgadjiev.ormnext.core.loader;

import ru.saidgadjiev.ormnext.core.cache.CacheHolder;
import ru.saidgadjiev.ormnext.core.cache.ObjectCache;

import java.util.Optional;

/**
 * Cache helper.
 *
 * @author said gadjiev
 */
public class CacheHelper {

    /**
     * Session cache holder.
     */
    private CacheHolder sessionFactoryCacheHolder;

    /**
     * Session factory holder.
     */
    private CacheHolder sessionCacheHolder;

    /**
     * Create a new helper.
     * @param sessionFactoryCacheHolder session factory cache holder
     * @param sessionCacheHolder session cache holder
     */
    public CacheHelper(CacheHolder sessionFactoryCacheHolder, CacheHolder sessionCacheHolder) {
        this.sessionFactoryCacheHolder = sessionFactoryCacheHolder;
        this.sessionCacheHolder = sessionCacheHolder;
    }

    /**
     * Save object to cache.
     * @param id object id
     * @param object object instance
     */
    public void saveToCache(Object id, Object object) {
        if (sessionFactoryCacheHolder.isCaching(object.getClass())) {
            ObjectCache objectCache = sessionFactoryCacheHolder.getObjectCache();

            objectCache.put(object.getClass(), id, object);
        }
        sessionCacheHolder.getObjectCache().put(object.getClass(), id, object);
    }

    /**
     * Remove object from cache.
     * @param objectClass object class
     * @param id object id
     */
    public void delete(Class<?> objectClass, Object id) {
        if (sessionFactoryCacheHolder.isCaching(objectClass)) {
            sessionFactoryCacheHolder.getObjectCache().invalidate(objectClass, id);
        }
        sessionCacheHolder.getObjectCache().invalidate(objectClass, id);
    }

    /**
     * Retrieve object instance from cache.
     * @param objectClass object class
     * @param id object id
     * @return cached instance
     */
    public Optional<Object> get(Class<?> objectClass, Object id) {
        if (sessionCacheHolder.getObjectCache().contains(objectClass, id)) {
            return Optional.of(sessionCacheHolder.getObjectCache().get(objectClass, id));
        }
        if (sessionFactoryCacheHolder.isCaching(objectClass)) {
            ObjectCache objectCache = sessionFactoryCacheHolder.getObjectCache();

            return Optional.ofNullable(objectCache.get(objectClass, id));
        }

        return Optional.empty();
    }
}
