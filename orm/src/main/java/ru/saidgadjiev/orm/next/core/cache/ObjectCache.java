package ru.saidgadjiev.orm.next.core.cache;

public interface ObjectCache {

    <T> void registerClass(Class<T> tClass);

    <T, ID> void put(Class<T> tClass, ID id, T data);

    <T, ID> T get(Class<T> tClass, ID id);

    <T, ID> boolean contains(Class<T> tClass, ID id);

    <T, ID> void invalidate(Class<T> tClass, ID id);

    <T> void invalidateAll(Class<T> tClass);

    void invalidateAll();

    <T> long size(Class<T> tClass);

    long sizeAll();
}
