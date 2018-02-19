package ru.saidgadjiev.orm.next.core.cache;

import ru.saidgadjiev.up.cache.core.Cache;
import ru.saidgadjiev.up.cache.core.CacheBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("CPD-START")
public class LRUObjectCache implements ObjectCache {

    private Map<Class<?>, Cache<Object, Object>> cache = new ConcurrentHashMap<>();

    private int maxSize;

    public LRUObjectCache(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public <T> void registerClass(Class<T> tClass) {
        cache.computeIfAbsent(tClass, k -> CacheBuilder.newLRUCacheBuilder().maxSize(maxSize).build());

    }

    @Override
    public <T, ID> void put(Class<T> tClass, ID id, T data) {
        Cache<Object, Object> objectCache = cache.get(tClass);

        if (objectCache != null) {
            objectCache.put(id, data);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, ID> T get(Class<T> tClass, ID id) {
        Cache<Object, Object> objectCache = cache.get(tClass);

        if (objectCache == null) {
            return null;
        }
        Object data = objectCache.get(id);

        return (T) data;
    }

    @Override
    public<T, ID> boolean contains(Class<T> tClass, ID id) {
        Cache<Object, Object> objectCache = cache.get(tClass);

        if (objectCache == null) {
            return false;
        }
        return objectCache.contains(id);
    }

    @Override
    public <T, ID> void invalidate(Class<T> tClass, ID id) {
        Cache<Object, Object> objectCache = cache.get(tClass);

        if (objectCache == null) {
            return;
        }
        objectCache.invalidate(id);
    }

    @Override
    public<T> void invalidateAll(Class<T> tClass) {
        Cache<Object, Object> objectCache = cache.get(tClass);

        if (objectCache == null) {
            return;
        }
        objectCache.invalidateAll();
    }

    @Override
    public void invalidateAll() {
        cache.forEach((key, value) -> value.invalidateAll());
    }

    @Override
    public <T> long size(Class<T> tClass) {
        Cache<Object, Object> objectCache = cache.get(tClass);

        if (objectCache == null) {
            return 0;
        }

        return objectCache.size();
    }

    @Override
    public long sizeAll() {
        long count = 0;

        for (Cache<Object, Object> cache: cache.values()) {
            count += cache.size();
        }

        return count;
    }

}
