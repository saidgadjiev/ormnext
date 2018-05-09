package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.cache.CacheContext;
import ru.saidgadjiev.ormnext.core.cache.ObjectCache;
import ru.saidgadjiev.ormnext.core.cache.ReferenceObjectCache;
import ru.saidgadjiev.ormnext.core.support.ConnectionSource;
import ru.saidgadjiev.ormnext.core.support.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;

import java.sql.SQLException;

public class SessionManagerImpl implements SessionManager {

    private final ConnectionSource dataSource;

    private CacheContext cacheContext = new CacheContext();

    private MetaModel metaModel;

    private DatabaseEngine databaseEngine;

    public SessionManagerImpl(ConnectionSource dataSource, MetaModel metaModel, DatabaseEngine databaseEngine) {
        this.dataSource = dataSource;
        this.metaModel = metaModel;
        this.databaseEngine = databaseEngine;

        this.metaModel.init(this);
    }

    @Override
    public Session createSession() throws SQLException {
        DatabaseConnection databaseConnection = dataSource.getConnection();

        return new SessionImpl(dataSource,databaseConnection, databaseEngine, metaModel, cacheContext,this);
    }

    @Override
    public MetaModel getMetaModel() {
        return metaModel;
    }

    @Override
    public void setObjectCache(ObjectCache objectCache) {
        if (objectCache != null) {
            cacheContext
                    .objectCache(objectCache);

            for (Class<?> clazz : metaModel.getPersistentClasses()) {
                cacheContext.caching(clazz, true);
                objectCache.registerClass(clazz);
            }
        }
    }

    @Override
    public void enableDefaultCache() {
        ObjectCache objectCache = new ReferenceObjectCache();

        cacheContext
                .objectCache(objectCache);

        for (Class<?> clazz : metaModel.getPersistentClasses()) {
            cacheContext.caching(clazz, true);
            objectCache.registerClass(clazz);
        }
    }

    @Override
    public void close() throws SQLException {
        dataSource.close();
    }
}
