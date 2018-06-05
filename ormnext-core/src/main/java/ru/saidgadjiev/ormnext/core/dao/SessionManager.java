package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.cache.ObjectCache;
import ru.saidgadjiev.ormnext.core.connection.source.ConnectionSource;
import ru.saidgadjiev.ormnext.core.loader.CacheHelper;
import ru.saidgadjiev.ormnext.core.loader.EntityLoader;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;

import java.sql.SQLException;

import static ru.saidgadjiev.ormnext.core.loader.EntityLoader.*;

/**
 * The main contract here is the creation of {@link Session} instances.
 * Usually an application has one instance of {@link SessionManager}.
 *
 * @author said gadjiev
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

    /**
     * Return current meta model.
     *
     * @return current entities meta model
     * @see MetaModel
     */
    MetaModel getMetaModel();

    /**
     * Provide object cache.
     *
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
     *
     * @return current database engine
     * @see DatabaseEngine
     */
    DatabaseEngine getDatabaseEngine();

    /**
     * Cache helper.
     *
     * @return current cache helper
     * @see CacheHelper
     */
    CacheHelper cacheHelper();

    /**
     * Return entity loader associated with requested loader type {@link Loader}.
     *
     * @param loader target loader
     * @return entity loader
     */
    EntityLoader loader(Loader loader);

    /**
     * Close all resources in this {@link SessionManager}.
     *
     * @throws SQLException on any SQL problems
     */
    void close() throws SQLException;
}
