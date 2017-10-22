package ru.said.miami.cache.core;

public interface Cache<K, V> {

    void put(K key, V value);

    void invalidate(K key);

    void invalidateAll();

    V get(K key);

    void update(K key, V value);

    boolean contains(K key);
}
