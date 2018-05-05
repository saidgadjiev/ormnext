package ru.saidgadjiev.ormnext.core.cache;

public interface ObjectCache {

    void registerClass(Class<?> tClass);

    void put(Class<?> tClass, Object id, Object data);

    Object get(Class<?> tClass, Object id);

    boolean contains(Class<?> tClass, Object id);

    void invalidate(Class<?> tClass, Object id);

    void invalidateAll(Class<?> tClass);

    void invalidateAll();

    long size(Class<?> tClass);

    long sizeAll();
}
