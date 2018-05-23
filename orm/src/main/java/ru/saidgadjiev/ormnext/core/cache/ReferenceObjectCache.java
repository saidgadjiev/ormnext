package ru.saidgadjiev.ormnext.core.cache;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReferenceObjectCache<ID, T> implements ObjectCache<ID, T> {

    private Map<Class<T>, Map<ID, SoftReference<T>>> cache = new ConcurrentHashMap<>();

    @Override
    public void registerClass(Class<T> tClass) {
        cache.computeIfAbsent(tClass, k -> new HashMap<>());
    }

    @Override
    public void put(Class<T> tClass, ID id, T data) {
        Map<ID, SoftReference<T>> objectCache = cache.get(tClass);

        if (objectCache != null) {
            objectCache.put(id, new SoftReference<>(data));
        }
    }

    @Override
    public T get(Class<T> tClass, ID id) {
        Map<ID, SoftReference<T>> objectCache = cache.get(tClass);

        if (objectCache == null) {
            return null;
        }
        Reference<T> ref = objectCache.get(id);

        if (ref == null) {
            return null;
        }
        T obj = ref.get();

        if (obj == null) {
            objectCache.remove(id);

            return null;
        } else {
            return obj;
        }
    }

    @Override
    public boolean contains(Class<T> tClass, ID id) {
        Map<ID, SoftReference<T>> objectCache = cache.get(tClass);

        if (objectCache == null) {
            return false;
        }

        return objectCache.containsKey(id);
    }

    @Override
    public void invalidate(Class<T> tClass, ID id) {
        Map<ID, SoftReference<T>> objectCache = cache.get(tClass);

        if (objectCache == null) {
            return;
        }
        objectCache.remove(id);
    }

    @Override
    public void invalidateAll(Class<T> tClass) {
        Map<ID, SoftReference<T>> objectCache = cache.get(tClass);

        if (objectCache == null) {
            return;
        }
        objectCache.clear();
    }

    @Override
    public void invalidateAll() {
        cache.forEach((key, value) -> value.clear());
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public long size(Class<T> tClass) {
        Map<ID, SoftReference<T>> objectCache = cache.get(tClass);

        if (objectCache == null) {
            return 0;
        }

        return objectCache.size();
    }

}
