package ru.said.miami.orm.core.cache;

import ru.said.miami.cache.core.Cache;
import ru.said.miami.cache.core.CacheBuilder;

public class LRUObjectCache implements ObjectCache {

    private Cache<Object, Object> cache;

    public LRUObjectCache(int maxSize) {
        cache = CacheBuilder.newLRUCacheBuilder().maxSize(maxSize).build();
    }

    @Override
    public <T, ID> void put(ID id, T data) {
        cache.put(id, data);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, ID> T get(ID id) {
        Object data = cache.get(id);

        return (T) data;
    }

    @Override
    public<ID> boolean contains(ID id) {
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
