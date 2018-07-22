package ru.saidgadjiev.ormnext.core.dao;

/**
 * Session manager with cache api.
 *
 * @author Said Gadjiev
 */
public interface CacheSessionManager extends SessionManager {

    /**
     * Put object to cache.
     *
     * @param id target id
     * @param data target data
     */
    void putToCache(Object id, Object data);
}
