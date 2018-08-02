package ru.saidgadjiev.ormnext.core.cache;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.dao.DatabaseEngine;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.DeleteStatement;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.Query;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.UpdateStatement;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;

import java.io.Closeable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Interface for ormnext cache.
 *
 * @author Said Gadjiev
 */
public interface Cache extends Closeable {

    /**
     * Initialize cache.
     *
     * @param metaModel      target meta model
     * @param databaseEngine target database engine
     */
    void init(MetaModel metaModel, DatabaseEngine<?> databaseEngine);

    /**
     * Save object in cache.
     *
     * @param object target object
     */
    void create(Object object);

    /**
     * Save objects in cache.
     *
     * @param objects target objects
     */
    void create(Collection<Object> objects);

    /**
     * Save object in cache.
     *
     * @param id target id
     * @param data target object
     */
    void putToCache(Object id, Object data);

    /**
     * Cache query for id result.
     *
     * @param id   target id
     * @param data target data
     */
    void cacheQueryForId(Object id, Object data);

    /**
     * Retrieve object from cache.
     *
     * @param tClass target object class
     * @param id     target object id
     * @return optional object
     */
    Optional<Object> queryForId(Class<?> tClass, Object id);

    /**
     * Cache query for all results.
     *
     * @param objects target result
     */
    void cacheQueryForAll(Collection<Object> objects);

    /**
     * Retrieve all objects from cache by type.
     *
     * @param tClass target object type
     * @return all objects
     */
    Optional<List<Object>> queryForAll(Class<?> tClass);

    /**
     * Update object.
     *
     * @param object target object
     */
    void update(Object object);

    /**
     * Delete object from cache.
     *
     * @param tClass target object type
     * @param id     target object id
     */
    void deleteById(Class<?> tClass, Object id);

    /**
     * Refresh object.
     *
     * @param object target object
     */
    void refresh(Object object);

    /**
     * Delete object from cache.
     *
     * @param object target object
     */
    void delete(Object object);

    /**
     * Handle delete statement.
     *
     * @param deleteStatement target delete statement
     */
    void delete(DeleteStatement deleteStatement);

    /**
     * Handle update statement.
     *
     * @param updateStatement target update statement
     */
    void update(UpdateStatement updateStatement);

    /**
     * Cache count off result.
     *
     * @param entityType target entity type
     * @param countOff   target result
     */
    void cacheCountOff(Class<?> entityType, long countOff);

    /**
     * Retrieve count off result from cache.
     *
     * @param entityType target entity type
     * @return count off result if it is present
     */
    Optional<Long> countOff(Class<?> entityType);

    /**
     * Cache exist.
     *
     * @param entityType target entity type
     * @param id         target checked id
     * @param exist      exist result
     */
    void cacheExist(Class<?> entityType, Object id, Boolean exist);

    /**
     * Retrieve exist result from cache.
     *
     * @param entityType target entity type
     * @param id         checked id
     * @return exist result if it is present
     */
    Optional<Boolean> exist(Class<?> entityType, Object id);

    /**
     * Cache list by select statement.
     *
     * @param selectStatement target statement
     * @param objects         target results
     */
    void cacheList(SelectStatement<?> selectStatement, List<Object> objects);

    /**
     * Retrieve objects from cache by {@link SelectStatement}.
     *
     * @param selectStatement target statement
     * @return objects if it is present
     */
    Optional<List<Object>> list(SelectStatement<?> selectStatement);

    /**
     * Cache query for long result.
     *
     * @param selectStatement target statement
     * @param result          target result
     */
    void cacheQueryForLong(SelectStatement<?> selectStatement, long result);

    /**
     * Retrieve cached value for query for long.
     *
     * @param selectStatement target query
     * @return long value which return query
     * @see SelectStatement
     */
    Optional<Long> queryForLong(SelectStatement<?> selectStatement);

    /**
     * Retrieve cached query results.
     *
     * @param query target query
     * @return query results
     */
    Optional<DatabaseResults> query(Query query);

    /**
     * Cache query results.
     *
     * @param query target query
     * @param results target query results
     */
    void cacheQuery(Query query, DatabaseResults results);

    /**
     * Enable default cache.
     */
    void enableDefaultCache();

    /**
     * Set cache for type.
     *
     * @param entityType  target entity type
     * @param objectCache target cache
     */
    void setCache(Class<?> entityType, ObjectCache objectCache);

    /**
     * Set caches for type.
     *
     * @param entityTypes target entity types
     * @param objectCache target object cache
     */
    void setCache(Class<?>[] entityTypes, ObjectCache objectCache);

    /**
     * Retrieve object cache.
     *
     * @param entityType target entity type
     * @return object cache
     */
    ObjectCache getCache(Class<?> entityType);

    /**
     * Evict api.
     *
     * @return cache evict api
     */
    CacheEvict evictApi();

    /**
     * Close cache resources.
     */
    void close();
}
