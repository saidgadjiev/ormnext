package ru.saidgadjiev.orm.next.core.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CacheContext {

    private ObjectCache objectCache;

    private Map<Class<?>, Boolean> caching = new HashMap<>();

    public CacheContext(ObjectCache objectCache) {
        this.objectCache = objectCache;
    }

    public CacheContext() {
    }

    public Optional<ObjectCache> getObjectCache() {
        return Optional.ofNullable(objectCache);
    }

    public CacheContext objectCache(ObjectCache objectCache) {
        this.objectCache = objectCache;

        return this;
    }

    public boolean isCaching(Class<?> clazz) {
        return caching.get(clazz);
    }

    public CacheContext caching(Class<?> clazz, boolean caching) {
        this.caching.put(clazz, caching);

        return this;
    }
}
