package ru.said.miami.cache.core;


public class CacheBuilder<K, V> {

    private CacheBuilder() {}

    private int maxSize;

    public static CacheBuilder<Object, Object> newBuilder() {
        return new CacheBuilder<>();
    }

    public int getMaxSize() {
        return maxSize;
    }

    public CacheBuilder<K, V> maxSize(int maxSize) {
        this.maxSize = maxSize;

        return this;
    }

    public <K1 extends K, V1 extends V> Cache<K1, V1> build() {
        return new LRUCache<>(this);
    }
}
