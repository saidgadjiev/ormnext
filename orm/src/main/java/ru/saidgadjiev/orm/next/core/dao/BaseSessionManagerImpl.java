package ru.saidgadjiev.orm.next.core.dao;

import ru.saidgadjiev.orm.next.core.cache.CacheContext;
import ru.saidgadjiev.orm.next.core.cache.ObjectCache;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;

import java.util.Optional;

/**
 * Базовый класс для DAO. Используется в DaoBuilder
 */
public class BaseSessionManagerImpl implements SessionManager {

    private final ConnectionSource dataSource;

    private CacheContext cacheContext = new CacheContext();

    public BaseSessionManagerImpl(ConnectionSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void caching(boolean flag, Class<?>... classes) {
        Optional<ObjectCache> objectCache = cacheContext.getObjectCache();

        objectCache.ifPresent(objectCache1 -> {
            for (Class<?> clazz : classes) {
                cacheContext.caching(clazz, flag);
                objectCache1.registerClass(clazz);
            }
        });

    }

    @Override
    public void setObjectCache(ObjectCache objectCache, Class<?>... classes) {
        cacheContext.getObjectCache().ifPresent(ObjectCache::invalidateAll);
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
    public Session getSession() {
        return new SessionImpl(dataSource, cacheContext);
    }
}
