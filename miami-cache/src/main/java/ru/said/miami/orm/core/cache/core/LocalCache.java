package ru.said.miami.orm.core.cache.core;

import java.util.HashMap;
import java.util.Map;

public class LocalCache<K, V> implements Cache<K, V> {

    private Map<K, V> cache = new HashMap<>();

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public void remove(K key) {
        cache.remove(key);
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }
}
