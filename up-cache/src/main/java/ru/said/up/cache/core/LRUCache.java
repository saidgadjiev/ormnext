package ru.said.up.cache.core;


/**
 * Created by said on 22.10.17.
 */
public class LRUCache<K, V> implements Cache<K, V> {

    private LRUHashMap<K, V> cacheMap;

    public LRUCache(CacheBuilder.LRUCacheBuilder<? super K, ? super V> builder) {
        cacheMap = new LRUHashMap<>(builder.getMaxSize());
    }

    @Override
    public void put(K key, V value) {
        cacheMap.put(key, value);
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
    public boolean contains(K key) {
        return cacheMap.containsKey(key);
    }

    @Override
    public long size() {
        return cacheMap.size();
    }
}
