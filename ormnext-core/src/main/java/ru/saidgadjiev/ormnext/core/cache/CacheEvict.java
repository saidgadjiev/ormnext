package ru.saidgadjiev.ormnext.core.cache;

import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;

/**
 * Cache evict api.
 *
 * @author Said Gadjiev
 */
public interface CacheEvict {

    /**
     * Evict list results {@link ru.saidgadjiev.ormnext.core.dao.Dao#list(SelectStatement)}.
     *
     * @param selectStatement target select statement
     */
    void evictList(SelectStatement<?> selectStatement);

    /**
     * Evict all list results {@link ru.saidgadjiev.ormnext.core.dao.Dao#list(SelectStatement)} by entity type.
     *
     * @param entityType target entity type
     */
    void evictList(Class<?> entityType);

    /**
     * Evict all list results {@link ru.saidgadjiev.ormnext.core.dao.Dao#list(SelectStatement)}.
     */
    void evictList();

    /**
     * Evict query for long result {@link ru.saidgadjiev.ormnext.core.dao.Dao#queryForAll(Class)} by entity type.
     *
     * @param entityType target entity type
     */
    void evictQueryForLong(Class<?> entityType);

    /**
     * Evict query for long result {@link ru.saidgadjiev.ormnext.core.dao.Dao#queryForLong(SelectStatement)}.
     *
     * @param selectStatement target select statement
     */
    void evictQueryForLong(SelectStatement<?> selectStatement);

    /**
     * Evict all query for long results {@link ru.saidgadjiev.ormnext.core.dao.Dao#queryForLong(SelectStatement)}.
     */
    void evictQueryForLong();

    /**
     * Evict unique result {@link ru.saidgadjiev.ormnext.core.dao.Dao#uniqueResult(SelectStatement)}.
     *
     * @param selectStatement target select statement
     */
    void evictUniqueResult(SelectStatement<?> selectStatement);

    /**
     * Evict unique results {@link ru.saidgadjiev.ormnext.core.dao.Dao#uniqueResult(SelectStatement)} by entity type.
     *
     * @param entityType target entity type
     */
    void evictUniqueResult(Class<?> entityType);

    /**
     * Evict all unique results {@link ru.saidgadjiev.ormnext.core.dao.Dao#uniqueResult(SelectStatement)}.
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
     * Evict limited list results {@link ru.saidgadjiev.ormnext.core.dao.Dao#list(SelectStatement)}.
     * It mean select statement with limit offset.
     *
     * @param selectStatement target select statement
     */
    void evictLimitedList(SelectStatement<?> selectStatement);

    /**
     * Evict limit offset list results {@link ru.saidgadjiev.ormnext.core.dao.Dao#list(SelectStatement)} by entity type.
     *
     * @param entityType target entity type
     */
    void evictLimitedList(Class<?> entityType);

    /**
     * Evict limit offset list results {@link ru.saidgadjiev.ormnext.core.dao.Dao#list(SelectStatement)}.
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

    /**
     * Retrieve object cache.
     *
     * @param entityType target entity type
     * @return object cache
     */
    ObjectCache getCache(Class<?> entityType);
}
