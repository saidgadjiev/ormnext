package ru.said.orm.next.core.cache;

import ru.said.up.cache.core.Cache;
import ru.said.up.cache.core.CacheBuilder;

import java.lang.ref.SoftReference;

public class ReferenceObjectCache implements ObjectCache {

    private Cache<Object, SoftReference<Object>> cache = CacheBuilder.newRefenceCacheBuilder().build();

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
        return cache.contains(id);
    }

    @Override
    public <ID> void invalidate(ID id) {
        cache.invalidate(id);
    }

    @Override
    public void invalidateAll() {
        cache.invalidateAll();
    }
}
