package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.cache.ObjectCache;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;

import java.sql.SQLException;

/**
 * The main contract here is the creation of {@link Session} instances.
 * Usually an application has one instance of {@link SessionManager}.
 *
 * @author said gadjiev
 */
public interface SessionManager extends AutoCloseable {

    /**
     * Open new session which obtain new {@link ru.saidgadjiev.ormnext.core.connection_source.DatabaseConnection} from
     * {@link ru.saidgadjiev.ormnext.core.connection_source.ConnectionSource} as needed to perform requested work.
     * @return created session
     * @throws SQLException on any SQL problems
     * @see Session
     */
    Session createSession() throws SQLException;

    /**
     * Return current meta model.
     * @return current entities meta model
     * @see MetaModel
     */
    MetaModel getMetaModel();

    /**
     * Provide object cache.
     * @param objectCache target object cache
     * @see ObjectCache
     */
    void setObjectCache(ObjectCache objectCache);

    /**
     * Enable default object cache. Default cache implementation is
     * {@link ru.saidgadjiev.ormnext.core.cache.ReferenceObjectCache}.
     */
    void enableDefaultCache();

    /**
     * Return current database engine.
     * @return current database engine
     * @see DatabaseEngine
     */
    DatabaseEngine getDatabaseEngine();

    /**
     * Close all resources in this {@link SessionManager}.
     * @throws SQLException on any SQL problems
     */
    void close() throws SQLException;
}
