package ru.saidgadjiev.ormnext.core.cache;

/**
 * Cache evict api.
 *
 * @author Said Gadjiev
 */
public interface CacheEvict {

    /**
     * Evict all list results {@link ru.saidgadjiev.ormnext.core.dao.Dao#list)} by entity type.
     *
     * @param entityType target entity type
     */
    void evictList(Class<?> entityType);

    /**
     * Evict all list results {@link ru.saidgadjiev.ormnext.core.dao.Dao#list)}.
     */
    void evictList();

    /**
     * Evict query for long result {@link ru.saidgadjiev.ormnext.core.dao.Dao#queryForAll(Class)} by entity type.
     *
     * @param entityType target entity type
     */
    void evictQueryForLong(Class<?> entityType);

    /**
     * Evict all query for long results {@link ru.saidgadjiev.ormnext.core.dao.Dao#queryForLong)}.
     */
    void evictQueryForLong();

    /**
     * Evict unique results {@link ru.saidgadjiev.ormnext.core.dao.Dao#uniqueResult)} by entity type.
     *
     * @param entityType target entity type
     */
    void evictUniqueResult(Class<?> entityType);

    /**
     * Evict all unique results {@link ru.saidgadjiev.ormnext.core.dao.Dao#uniqueResult)}.
     */
    void evictUniqueResult();

    /**
     * Evict countOff {@link ru.saidgadjiev.ormnext.core.dao.Dao#countOff(Class)} by entity type.
     *
     * @param entityType target entity type
     */
    void evictCountOff(Class<?> entityType);

    /**
     * Evict all countOff results {@link ru.saidgadjiev.ormnext.core.dao.Dao#countOff(Class)}.
     */
    void evictCountOff();

    /**
     * Evict exist {@link ru.saidgadjiev.ormnext.core.dao.Dao#exist(Class, Object)} by entity type.
     *
     * @param entityType target entity type
     */
    void evictExist(Class<?> entityType);

    /**
     * Evict all exist results {@link ru.saidgadjiev.ormnext.core.dao.Dao#exist(Class, Object)}.
     */
    void evictExist();

    /**
     * Evict query for all results {@link ru.saidgadjiev.ormnext.core.dao.Dao#queryForAll(Class)} by entity type.
     *
     * @param entityType target entity type
     */
    void evictQueryForAll(Class<?> entityType);

    /**
     * Evict all query for long results {@link ru.saidgadjiev.ormnext.core.dao.Dao#queryForAll(Class)}.
     */
    void evictQueryForAll();

    /**
     * Evict limit offset list results {@link ru.saidgadjiev.ormnext.core.dao.Dao#list)} by entity type.
     *
     * @param entityType target entity type
     */
    void evictLimitedList(Class<?> entityType);

    /**
     * Evict limit offset list results {@link ru.saidgadjiev.ormnext.core.dao.Dao#list)}.
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
