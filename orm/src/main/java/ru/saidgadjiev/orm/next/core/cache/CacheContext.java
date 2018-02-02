package ru.saidgadjiev.orm.next.core.cache;

import java.util.Optional;

public class CacheContext {

    private ObjectCache objectCache;

    private boolean caching;

    public CacheContext(ObjectCache objectCache, boolean caching) {
        this.objectCache = objectCache;
        this.caching = caching;
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

    public boolean isCaching() {
        return caching;
    }

    public CacheContext caching(boolean caching) {
        this.caching = caching;

        return this;
    }
}
