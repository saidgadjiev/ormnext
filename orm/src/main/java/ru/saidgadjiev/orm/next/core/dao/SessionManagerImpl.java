package ru.saidgadjiev.orm.next.core.dao;

import ru.saidgadjiev.orm.next.core.cache.CacheContext;
import ru.saidgadjiev.orm.next.core.cache.ObjectCache;
import ru.saidgadjiev.orm.next.core.table.internal.metamodel.MetaModel;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;

import java.util.Collection;

/**
 * Базовый класс для DAO. Используется в DaoBuilder
 */
public class SessionManagerImpl implements SessionManager {

    private final ConnectionSource dataSource;

    private CacheContext cacheContext = new CacheContext();

    private Session session = null;

    private MetaModel metaModel;

    SessionManagerImpl(ConnectionSource dataSource, Collection<Class<?>> entityClasses) {
        this.dataSource = dataSource;
        this.metaModel = new MetaModel(entityClasses);
    }

    @Override
    public void caching(boolean flag, Class<?>... classes) {
        ObjectCache objectCache = cacheContext.getObjectCache();

        for (Class<?> clazz : classes) {
            cacheContext.caching(clazz, flag);
            objectCache.registerClass(clazz);
        }
    }

    @Override
    public void setObjectCache(ObjectCache objectCache, Class<?>... classes) {
        cacheContext.getObjectCache().invalidateAll();
        if (objectCache != null) {
            cacheContext
                    .objectCache(objectCache);

            for (Class<?> clazz : classes) {
                cacheContext.caching(clazz, true);
                objectCache.registerClass(clazz);
            }
        }
    }

    @Override
    public ConnectionSource getDataSource() {
        return dataSource;
    }

    @Override
    public Session getCurrentSession() {
        if (session == null) {
            session = new SessionImpl(this, cacheContext);
        }

        return session;
    }

    @Override
    public MetaModel getMetaModel() {
        return metaModel;
    }
}
