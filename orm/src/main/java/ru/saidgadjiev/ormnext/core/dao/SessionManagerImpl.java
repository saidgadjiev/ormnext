package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.cache.CacheAccess;
import ru.saidgadjiev.ormnext.core.cache.ObjectCache;
import ru.saidgadjiev.ormnext.core.cache.ReferenceObjectCache;
import ru.saidgadjiev.ormnext.core.support.ConnectionSource;
import ru.saidgadjiev.ormnext.core.support.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;

import java.sql.SQLException;

/**
 * Implementation of {@link SessionManager}.
 */
public class SessionManagerImpl implements SessionManager {

    /**
     * Connection source. Use for obtain new database connection.
     */
    private final ConnectionSource<?> dataSource;

    /**
     * Cache access helper. It hold object cache.
     * @see CacheAccess
     */
    private final CacheAccess cacheAccess = new CacheAccess();

    /**
     * Meta model.
     * @see MetaModel
     */
    private MetaModel metaModel;

    /**
     * Database engine.
     * @see DatabaseEngine
     */
    private DatabaseEngine databaseEngine;

    /**
     * Create new instance from requested options. It can be create only from {@link SessionManagerBuilder}.
     * @param connectionSource target connection source
     * @param metaModel target meta model
     * @param databaseEngine target database engine
     */
    SessionManagerImpl(ConnectionSource<?> connectionSource, MetaModel metaModel, DatabaseEngine databaseEngine) {
        this.dataSource = connectionSource;
        this.metaModel = metaModel;
        this.databaseEngine = databaseEngine;

        this.metaModel.init(this);
    }

    @Override
    public Session createSession() throws SQLException {
        DatabaseConnection<?> databaseConnection = dataSource.getConnection();

        return new SessionImpl(dataSource, databaseConnection, cacheAccess, this);
    }

    @Override
    public MetaModel getMetaModel() {
        return metaModel;
    }

    @Override
    public void setObjectCache(ObjectCache objectCache) {
        if (objectCache != null) {
            cacheAccess
                    .objectCache(objectCache);

            for (Class<?> clazz : metaModel.getPersistentClasses()) {
                cacheAccess.caching(clazz, true);
                objectCache.registerClass(clazz);
            }
        }
    }

    @Override
    public void enableDefaultCache() {
        ObjectCache objectCache = new ReferenceObjectCache();

        cacheAccess
                .objectCache(objectCache);

        for (Class<?> clazz : metaModel.getPersistentClasses()) {
            cacheAccess.caching(clazz, true);
            objectCache.registerClass(clazz);
        }
    }

    @Override
    public DatabaseEngine getDatabaseEngine() {
        return databaseEngine;
    }

    @Override
    public void close() throws SQLException {
        cacheAccess.close();
        dataSource.close();
    }
}
