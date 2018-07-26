package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.cache.Cache;
import ru.saidgadjiev.ormnext.core.cache.CacheEvict;
import ru.saidgadjiev.ormnext.core.cache.ObjectCache;
import ru.saidgadjiev.ormnext.core.connection.source.ConnectionSource;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;

import java.sql.SQLException;

/**
 * The main contract here is the creation of {@link Session} instances.
 * Usually an application has one instance of {@link SessionManager}.
 *
 * @author Said Gadjiev
 */
public interface SessionManager extends AutoCloseable {

    /**
     * Open new session which obtain new {@link ru.saidgadjiev.ormnext.core.connection.DatabaseConnection} from
     * {@link ConnectionSource} as needed to perform requested work.
     *
     * @return created session
     * @throws SQLException on any SQL problems
     * @see Session
     */
    Session createSession() throws SQLException;

    Session currentSession() throws SQLException;

    /**
     * Return current meta model.
     *
     * @return current entities meta model
     * @see MetaModel
     */
    MetaModel getMetaModel();

    /**
     * Enable default object cache.
     */
    void enableDefaultCache();

    /**
     * Return current database engine.
     *
     * @param <T> connection type
     * @return current database engine
     * @see DatabaseEngine
     */
    <T> DatabaseEngine<T> getDatabaseEngine();

    /**
     * Upgrade framework by cache.
     *
     * @param cache target cache part
     */
    void upgrade(Cache cache);

    /**
     * Provide object cache.
     *
     * @param entityClass target entity class
     * @param objectCache target object cache
     * @see ObjectCache
     */
    void setObjectCache(Class<?> entityClass, ObjectCache objectCache);

    /**
     * Provide object cache.
     *
     * @param entityClass target entity classes
     * @param objectCache target object cache
     * @see ObjectCache
     */
    void setObjectCache(Class<?>[] entityClass, ObjectCache objectCache);

    /**
     * Return ormnext cache evict api.
     *
     * @return cache evict api
     */
    CacheEvict getCacheEvictApi();

    boolean isClosed();

    /**
     * Close all resources in this {@link SessionManager}.
     *
     * @throws SQLException on any SQL problems
     */
    void close() throws SQLException;
}
