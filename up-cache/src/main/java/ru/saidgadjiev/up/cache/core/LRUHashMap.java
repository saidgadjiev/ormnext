package ru.saidgadjiev.up.cache.core;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Map with lru item remove algorithm.
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 *
 * @author Said Gadjiev
 */
public class LRUHashMap<K, V> extends LinkedHashMap<K, V> {

    /**
     * Map default load factor.
     */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * Map capacity.
     */
    private final int capacity;

    /**
     * Constructor for empty LRUHashMap with initial capacity.
     * @param capacity initial map capacity
     */
    public LRUHashMap(int capacity) {
        super(capacity, DEFAULT_LOAD_FACTOR, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}
