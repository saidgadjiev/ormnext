package ru.saidgadjiev.ormnext.core.cache;

import java.util.Collection;

/**
 * Object cache.
 *
 * @author Said Gadjiev
 */
public interface ObjectCache {

    /**
     * Register class for use in cache. This will be called before any other method for the particular class is
     * called.
     *
     * @param tClass target entity class
     */
    void registerClass(Class<?> tClass);

    /**
     * Put an object in the cache that has a certain class and id.
     *
     * @param tClass target entity class
     * @param id     target entity object id
     * @param data   target entity object
     */
    void put(Class<?> tClass, Object id, Object data);

    /**
     * Lookup in the cache for an object of a certain class that has a certain id.
     *
     * @param tClass taret entity class
     * @param id     target entity id
     * @return entity object associated with id
     */
    Object get(Class<?> tClass, Object id);

    /**
     * Retrieve all cached objects by entity type.
     *
     * @param tClass target entity type
     * @return cached objects
     */
    Collection<Object> getAll(Class<?> tClass);

    /**
     * True if cache contains object cached with id {@code id}.
     *
     * @param tClass object class
     * @param id     id
     * @return true if cache contains object cached with id {@code id}
     */
    boolean contains(Class<?> tClass, Object id);

    /**
     * Remove cached value associated with id {@code id} from cache.
     *
     * @param tClass object class
     * @param id     id
     */
    void invalidate(Class<?> tClass, Object id);

    /**
     * Remove all cached value by requested type.
     *
     * @param tClass cached value type
     */
    void invalidateAll(Class<?> tClass);

    /**
     * Remove all cached values, but not remove registered classes.
     */
    void invalidateAll();

    /**
     * Remove all cached values and registered classes.
     */
    void clear();

    /**
     * Return actual current cache size by entity class.
     *
     * @param tClass target entity class
     * @return cache size
     */
    long size(Class<?> tClass);
}
