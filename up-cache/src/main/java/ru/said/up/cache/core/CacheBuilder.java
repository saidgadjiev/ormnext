package ru.said.up.cache.core;


public class CacheBuilder<K, V> {

    private CacheBuilder() {}

    public static LRUCacheBuilder<Object, Object> newLRUCacheBuilder() {
        return new LRUCacheBuilder<>();
    }

    public static ReferenceCacheBuilder<Object, Object> newRefenceCacheBuilder() {
        return new ReferenceCacheBuilder<>();
    }

    public static class LRUCacheBuilder<K, V> {

        private int maxSize = 16;

        public LRUCacheBuilder<K, V> maxSize(int maxSize) {
            this.maxSize = maxSize;

            return this;
        }

        public int getMaxSize() {
            return maxSize;
        }

        public <K1 extends K, V1 extends V> Cache<K1, V1> build() {
            return new LRUCache<>(this);
        }
    }

    public static class ReferenceCacheBuilder<K, V> {

        public <K1 extends K, V1 extends V> Cache<K1, V1> build() {
            return new ReferenceCache<>();
        }
    }
}
