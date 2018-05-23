package ru.saidgadjiev.ormnext.core.cache;

public interface ObjectCache<ID, T> {

    /**
     * Register class for use in cache. This will be called before any other method for the particular class is
     * called.
     * @param tClass target entity class
     */
    void registerClass(Class<T> tClass);

    /**
     * Put an object in the cache that has a certain class and id.
     * @param tClass target entity class
     * @param id target entity object id
     * @param data target entity object
     */
    void put(Class<T> tClass, ID id, T data);

    /**
     * Lookup in the cache for an object of a certain class that has a certain id.
     * @param tClass taret entity class
     * @param id target entity id
     * @return entity object associated with id
     */
    T get(Class<T> tClass, ID id);

    /**
     *
     * @param tClass
     * @param id
     * @return
     */
    boolean contains(Class<T> tClass, ID id);

    /**
     *
     * @param tClass
     * @param id
     */
    void invalidate(Class<T> tClass, ID id);

    /**
     *
     * @param tClass
     */
    void invalidateAll(Class<T> tClass);

    /**
     *
     */
    void invalidateAll();

    void clear();

    /**
     * Return actual current cache size by entity class.
     * @param tClass target entoty class
     * @return cache size
     */
    long size(Class<T> tClass);
}
