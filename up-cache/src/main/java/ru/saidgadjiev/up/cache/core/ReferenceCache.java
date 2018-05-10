package ru.saidgadjiev.up.cache.core;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache which stores objects with {@link SoftReference} to them.
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of mapped values
 *
 * @author Said Gadjiev
 */
public class ReferenceCache<K, V> implements Cache<K, V> {

    /**
     * Map for save cached objects.
     */
    private ConcurrentHashMap<K, SoftReference<V>> cache = new ConcurrentHashMap<>();

    @Override
    public void invalidateAll() {
        cache.clear();
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, new SoftReference<>(value));
    }

    @Override
    public void invalidate(K key) {
        cache.remove(key);
    }

    @Override
    public V get(K key) {
        Reference<V> ref = cache.get(key);
        if (ref == null) {
            return null;
        }
        V obj = ref.get();
        if (obj == null) {
            cache.remove(key);
            return null;
        } else {
            return obj;
        }
    }

    @Override
    public boolean contains(K key) {
        return cache.containsKey(key);
    }

    @Override
    public long size() {
        return cache.size();
    }
}
