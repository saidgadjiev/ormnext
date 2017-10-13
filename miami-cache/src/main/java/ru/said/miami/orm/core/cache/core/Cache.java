package ru.said.miami.orm.core.cache.core;

public interface Cache<K, V> {

    void put(K key, V value);

    void remove(K key);

    V get(K key);
}
