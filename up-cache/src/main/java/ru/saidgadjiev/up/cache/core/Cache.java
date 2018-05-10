package ru.saidgadjiev.up.cache.core;

/**
 * The Cache interface provide api for caching.
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 *
 * @author Said Gadjiev
 */
public interface Cache<K, V> {

    /**
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     */
    void put(K key, V value);

    void invalidate(K key);

    void invalidateAll();

    V get(K key);

    boolean contains(K key);

    long size();
}
