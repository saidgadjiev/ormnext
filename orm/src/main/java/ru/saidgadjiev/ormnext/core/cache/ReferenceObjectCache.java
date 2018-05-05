package ru.saidgadjiev.ormnext.core.cache;

import ru.saidgadjiev.up.cache.core.Cache;
import ru.saidgadjiev.up.cache.core.CacheBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReferenceObjectCache implements ObjectCache {

    private Map<Class<?>, Cache<Object, Object>> cache = new ConcurrentHashMap<>();

    @Override
    public void registerClass(Class<?> tClass) {
        cache.computeIfAbsent(tClass, k -> CacheBuilder.newRefenceCacheBuilder().build());
    }

    @Override
    public void put(Class<?> tClass, Object id, Object data) {
        Cache<Object, Object> objectCache = cache.get(tClass);

        if (objectCache != null) {
            objectCache.put(id, data);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object get(Class<?> tClass, Object id) {
        Cache<Object, Object> objectCache = cache.get(tClass);

        if (objectCache == null) {
            return null;
        }
        Object data = objectCache.get(id);

        return data;
    }

    @Override
    public boolean contains(Class<?> tClass, Object id) {
        Cache<Object, Object> objectCache = cache.get(tClass);

        if (objectCache == null) {
            return false;
        }
        return objectCache.contains(id);
    }

    @Override
    public void invalidate(Class<?> tClass, Object id) {
        Cache<Object, Object> objectCache = cache.get(tClass);

        if (objectCache == null) {
            return;
        }
        objectCache.invalidate(id);
    }

    @Override
    public void invalidateAll(Class<?> tClass) {
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
    public long size(Class<?> tClass) {
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
