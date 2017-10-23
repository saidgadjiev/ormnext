package ru.said.miami.orm.core.cache;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

public class ReferenceObjectCache implements ObjectCache {

    private ConcurrentHashMap<Object, SoftReference<Object>> cache = new ConcurrentHashMap<>();

    @Override
    public <T, ID> void put(ID id, T data) {
        cache.put(id, new SoftReference<>(data));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, ID> T get(ID id) {
        return (T) cache.get(id).get();
    }

    @Override
    public <ID> boolean contains(ID id) {
        return cache.containsKey(id);
    }

    @Override
    public <ID> void invalidate(ID id) {
        cache.remove(id);
    }

    @Override
    public void invalidateAll() {
        cache.clear();
    }
}
