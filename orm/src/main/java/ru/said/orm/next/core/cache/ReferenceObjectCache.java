package ru.said.orm.next.core.cache;

import ru.said.up.cache.core.Cache;
import ru.said.up.cache.core.CacheBuilder;

import java.lang.ref.SoftReference;

public class ReferenceObjectCache implements ObjectCache {

    private Cache<Object, SoftReference<Object>> cache = CacheBuilder.newRefenceCacheBuilder().build();

    @Override
    public <T> void registerClass(Class<T> tClass) {

    }

    @Override
    public <T, ID> void put(Class<T> tClass, ID id, T data) {
        cache.put(id, new SoftReference<>(data));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, ID> T get(Class<T> tClass, ID id) {
        return (T) cache.get(id).get();
    }

    @Override
    public <T, ID> boolean contains(Class<T> tClass, ID id) {
        return cache.contains(id);
    }

    @Override
    public <T, ID> void invalidate(Class<T> tClass, ID id) {
        cache.invalidate(id);
    }

    @Override
    public <T> void invalidateAll(Class<T> tClass) {

    }

    @Override
    public void invalidateAll() {
        cache.invalidateAll();
    }

    @Override
    public <T> long size(Class<T> tClass) {
        return 0;
    }

    @Override
    public <T> long sizeAll() {
        return 0;
    }
}
