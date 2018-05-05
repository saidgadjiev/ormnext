package ru.saidgadjiev.ormnext.core.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CacheContext {

    private ObjectCache objectCache;

    private Map<Class<?>, Boolean> caching = new HashMap<>();

    public CacheContext() {
    }

    public ObjectCache getObjectCache() {
        return objectCache;
    }

    public CacheContext objectCache(ObjectCache objectCache) {
        this.objectCache = objectCache;

        return this;
    }

    public boolean isCaching(Class<?> clazz) {
        if (caching.containsKey(clazz)) {
            return caching.get(clazz);
        }

        return false;
    }

    public CacheContext caching(Class<?> clazz, boolean caching) {
        this.caching.put(clazz, caching);

        return this;
    }

    public CacheContext caching(Collection<Class<?>> classes, boolean caching) {
        classes.forEach(aClass -> CacheContext.this.caching.put(aClass, caching));

        return this;
    }
}
