package ru.saidgadjiev.ormnext.core.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represent context for cache. It holds object cache instance {@link ObjectCache}
 * and can give answer some problems: is entity class caching, enable caching for entity class.
 *
 * @author Said Gadjiev
 */
public class CacheHolder implements AutoCloseable {

    /**
     * ObjectCache.
     */
    private ObjectCache objectCache;

    /**
     * Use for save information about entity class caching: true if caching, false if not.
     */
    private Map<Class<?>, Boolean> caching = new HashMap<>();

    /**
     * Get current object cache.
     *
     * @return object cache
     */
    public ObjectCache getObjectCache() {
        return objectCache;
    }

    /**
     * Provide object cache.
     *
     * @param objectCache target object cache
     * @return this instance for chain
     */
    public CacheHolder objectCache(ObjectCache objectCache) {
        this.objectCache = objectCache;

        return this;
    }

    /**
     * Check entity class is caching or not.
     *
     * @param clazz target entity class
     * @return true if caching else false
     */
    public boolean isCaching(Class<?> clazz) {
        if (caching.containsKey(clazz)) {
            return caching.get(clazz);
        }

        return false;
    }

    /**
     * Enable caching for entity class {@code clazz}.
     *
     * @param clazz   target class
     * @param caching true if cache need enable or else false
     */
    public void caching(Class<?> clazz, boolean caching) {
        this.caching.put(clazz, caching);
    }

    /**
     * Clear object cache.
     */
    public void close() {
        if (objectCache != null) {
            objectCache.clear();
        }
    }
}
