package ru.said.miami.cache.core;

import ru.said.miami.cache.core.cuncurrent.ConcurrentHashMap;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Map;


@SuppressWarnings("PMD")
public class ReferenceCache<K, V> implements Cache<K, V> {

    private ConcurrentHashMap<Object, SoftReference<Object>> cache = new ConcurrentHashMap<>();

    private CacheBuilder.ReferenceCacheBuilder<? super K, ? super V> referenceCacheBuilder;

    public ReferenceCache(CacheBuilder.ReferenceCacheBuilder<? super K, ? super V> referenceCacheBuilder) {
        this.referenceCacheBuilder = referenceCacheBuilder;
    }

    @Override
    public void invalidateAll() {
        cache.clear();
    }

    @Override
    public void put(K key, V value) {
        if (get(key) == null) {
            cache.put(key, new SoftReference<>(value));
        }
    }

    @Override
    public void invalidate(K key) {
        cache.remove(key);
    }

    @Override
    public V get(K key) {
        Reference<Object> ref = cache.get(key);
        if (ref == null) {
            return null;
        }
        V obj = (V) ref.get();
        if (obj == null) {
            cache.remove(key);
            return null;
        } else {
            return obj;
        }
    }

    @Override
    public void update(K key, V value) {
        cache.put(key, new SoftReference<>(value));
    }

    @Override
    public boolean contains(K key) {
        return false;
    }
}
