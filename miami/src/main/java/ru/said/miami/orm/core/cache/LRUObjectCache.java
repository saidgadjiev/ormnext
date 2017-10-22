package ru.said.miami.orm.core.cache;

import ru.said.miami.cache.core.Cache;
import ru.said.miami.cache.core.CacheBuilder;

import java.util.concurrent.ConcurrentHashMap;

public class LRUObjectCache implements ObjectCache {

    private Cache<Object, Object> cache;

    private int maxSize;

    public LRUObjectCache(int maxSize) {
        this.maxSize = maxSize;
        cache = CacheBuilder.newBuilder().maxSize(maxSize).build();
    }

    @Override
    public <T, ID> void put(ID id, T data) {
        cache.put(id, data);
    }

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
    public <ID> void remove(ID id) {
        cache.invalidate(id);
    }

}
