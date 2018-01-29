package ru.said.up.cache.core;

public interface Cache<K, V> {

    void put(K key, V value);

    void invalidate(K key);

    void invalidateAll();

    V get(K key);

    boolean contains(K key);

    long size();
}
