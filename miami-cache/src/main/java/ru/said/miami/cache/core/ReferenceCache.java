package ru.said.miami.cache.core;

import ru.said.miami.cache.core.cuncurrent.ConcurrentHashMap;

import java.lang.ref.SoftReference;

public class ReferenceCache<K, V> implements Cache<K, V> {

    private ConcurrentHashMap<Object, SoftReference<Object>> cache = new ConcurrentHashMap<>();

    public ReferenceCache(CacheBuilder.ReferenceCacheBuilder<? super K, ? super V> referenceCacheBuilder) {

    }

    @Override
    public void invalidateAll() {
        cache.clear();
    }

    @Override
    public void put(K key, V value) {

    }

    @Override
    public void invalidate(K key) {

    }

    @Override
    public V get(K key) {
        return null;
    }

    @Override
    public void update(K key, V value) {

    }

    @Override
    public boolean contains(K key) {
        return false;
    }
}
