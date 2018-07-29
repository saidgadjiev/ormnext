package ru.saidgadjiev.ormnext.core.cache;

/**
 * Cache evict api.
 *
 * @author Said Gadjiev
 */
public interface CacheEvict {

    /**
     * Evict all list results by entity type.
     *
     * @param entityType target entity type
     */
    void evictList(Class<?> entityType);

    /**
     * Evict all list results.
     */
    void evictList();

    /**
     * Evict query for long result by entity type.
     *
     * @param entityType target entity type
     */
    void evictQueryForLong(Class<?> entityType);

    /**
     * Evict all query for long results.
     */
    void evictQueryForLong();

    /**
     * Evict unique results by entity type.
     *
     * @param entityType target entity type
     */
    void evictUniqueResult(Class<?> entityType);

    /**
     * Evict all unique results.
     */
    void evictUniqueResult();

    /**
     * Evict countOff by entity type.
     *
     * @param entityType target entity type
     */
    void evictCountOff(Class<?> entityType);

    /**
     * Evict all countOff results.
     */
    void evictCountOff();

    /**
     * Evict exist by entity type.
     *
     * @param entityType target entity type
     */
    void evictExist(Class<?> entityType);

    /**
     * Evict all exist results.
     */
    void evictExist();

    /**
     * Evict query for all results by entity type.
     *
     * @param entityType target entity type
     */
    void evictQueryForAll(Class<?> entityType);

    /**
     * Evict all query for long results.
     */
    void evictQueryForAll();

    /**
     * Evict limit offset list results by entity type.
     *
     * @param entityType target entity type
     */
    void evictLimitedList(Class<?> entityType);

    /**
     * Evict limit offset list results.
     */
    void evictLimitedList();

    /**
     * Evict object by id.
     *
     * @param entityType target entity type
     * @param id         target object id
     */
    void evict(Class<?> entityType, Object id);

    /**
     * Evict all objects by entity type.
     *
     * @param entityType target entity type
     */
    void evict(Class<?> entityType);

    /**
     * Evict all cached objects.
     */
    void evict();

    /**
     * Evict all caches.
     *
     * @param entityType target entity type
     */
    void evictAll(Class<?> entityType);

    /**
     * Flush all caches.
     */
    void flush();
}
