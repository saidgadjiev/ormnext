package ru.said.miami.cache.core;

import ru.said.miami.cache.core.cuncurrent.LRUCacheHashMap;

/**
 * Created by said on 22.10.17.
 */
public class LRUCache<K, V> implements Cache<K, V> {

    private LRUCacheHashMap<K, V> cacheMap;

    public LRUCache(CacheBuilder.LRUCacheBuilder<? super K,  ? super V> builder) {
         cacheMap = new LRUCacheHashMap<>(builder.getMaxSize());
    }

    @Override
    public void put(K key, V value) {
        if (!cacheMap.containsKey(key)) {
            cacheMap.put(key, value);
        }
    }

    @Override
    public void invalidate(K key) {
        cacheMap.remove(key);
    }

    @Override
    public void invalidateAll() {
        cacheMap.clear();
    }

    @Override
    public V get(K key) {
        return cacheMap.get(key);
    }

    @Override
    public void update(K key, V value) {
        cacheMap.put(key, value);
    }

    @Override
    public boolean contains(K key) {
        return cacheMap.containsKey(key);
    }
}
