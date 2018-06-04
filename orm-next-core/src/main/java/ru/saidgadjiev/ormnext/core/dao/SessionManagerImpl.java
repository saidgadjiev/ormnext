package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.cache.CacheHolder;
import ru.saidgadjiev.ormnext.core.cache.ObjectCache;
import ru.saidgadjiev.ormnext.core.cache.ReferenceObjectCache;
import ru.saidgadjiev.ormnext.core.connection.source.ConnectionSource;
import ru.saidgadjiev.ormnext.core.connection.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.loader.BatchEntityLoader;
import ru.saidgadjiev.ormnext.core.loader.CacheHelper;
import ru.saidgadjiev.ormnext.core.loader.DefaultEntityLoader;
import ru.saidgadjiev.ormnext.core.loader.EntityLoader;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link SessionManager}.
 *
 * @author said gadjiev
 */
public class SessionManagerImpl implements SessionManager {

    /**
     * Connection source. Use for obtain new database connection.
     */
    private final ConnectionSource<?> dataSource;

    /**
     * Cache access helper. It hold object cache.
     *
     * @see CacheHolder
     */
    private final CacheHolder cacheHolder = new CacheHolder();

    /**
     * Cache helper.
     */
    private final CacheHelper cacheHelper;

    /**
     * Registered loaders.
     */
    private final Map<EntityLoader.Loader, EntityLoader> registeredLoaders;

    /**
     * Meta model.
     *
     * @see MetaModel
     */
    private MetaModel metaModel;

    /**
     * Database engine.
     *
     * @see DatabaseEngine
     */
    private DatabaseEngine<?> databaseEngine;

    /**
     * Create new instance from requested options. It can be create only from {@link SessionManagerBuilder}.
     *
     * @param connectionSource target connection source
     * @param metaModel        target meta model
     * @param databaseEngine   target database engine
     */
    SessionManagerImpl(ConnectionSource<?> connectionSource, MetaModel metaModel, DatabaseEngine<?> databaseEngine) {
        this.dataSource = connectionSource;
        this.metaModel = metaModel;
        this.databaseEngine = databaseEngine;
        this.cacheHelper = new CacheHelper(cacheHolder, metaModel);
        this.registeredLoaders = createLoaders();

        this.metaModel.init(this);
    }

    /**
     * Create entity loaders.
     *
     * @return entity loaders
     */
    private Map<EntityLoader.Loader, EntityLoader> createLoaders() {
        Map<EntityLoader.Loader, EntityLoader> registeredLoaders = new HashMap<>();

        registeredLoaders.put(EntityLoader.Loader.BATCH_LOADER, new BatchEntityLoader(this));
        registeredLoaders.put(EntityLoader.Loader.DEFAULT_LOADER, new DefaultEntityLoader(this));

        return registeredLoaders;
    }

    @Override
    public Session createSession() throws SQLException {
        DatabaseConnection<?> databaseConnection = dataSource.getConnection();

        return new SessionImpl(dataSource, databaseConnection, this);
    }

    @Override
    public MetaModel getMetaModel() {
        return metaModel;
    }

    @Override
    public void setObjectCache(ObjectCache objectCache) {
        if (objectCache != null) {
            cacheHolder
                    .objectCache(objectCache);

            for (Class<?> clazz : metaModel.getPersistentClasses()) {
                cacheHolder.caching(clazz, true);
                objectCache.registerClass(clazz);
            }
        }
    }

    @Override
    public void enableDefaultCache() {
        ObjectCache objectCache = new ReferenceObjectCache();

        cacheHolder
                .objectCache(objectCache);

        for (Class<?> clazz : metaModel.getPersistentClasses()) {
            cacheHolder.caching(clazz, true);
            objectCache.registerClass(clazz);
        }
    }

    @Override
    public DatabaseEngine getDatabaseEngine() {
        return databaseEngine;
    }

    @Override
    public CacheHelper cacheHelper() {
        return cacheHelper;
    }

    @Override
    public EntityLoader loader(EntityLoader.Loader loader) {
        return registeredLoaders.get(loader);
    }

    @Override
    public void close() throws SQLException {
        cacheHolder.close();
        dataSource.close();
    }
}
