package ru.saidgadjiev.ormnext.core.loader.rowreader.cache;

import ru.saidgadjiev.ormnext.core.cache.Cache;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;

/**
 * Context.
 *
 * @author Said Gadjiev
 */
public class CacheObjectContext {

    /**
     * Session.
     */
    private final Session session;

    /**
     * Cache.
     */
    private final Cache cache;

    /**
     * Metamdel.
     */
    private final MetaModel metaModel;

    /**
     * Create a new instance.
     *
     * @param session target session
     * @param cache target cache
     * @param metaModel target metamodel
     */
    public CacheObjectContext(Session session, Cache cache, MetaModel metaModel) {
        this.session = session;
        this.cache = cache;
        this.metaModel = metaModel;
    }

    /**
     * Return session.
     *
     * @return session
     */
    public Session getSession() {
        return session;
    }

    /**
     * Return cache.
     *
     * @return cache
     */
    public Cache getCache() {
        return cache;
    }

    /**
     * Return meta data.
     *
     * @return metadata
     */
    public DatabaseEntityMetadata<?> getMetadata(Class<?> entityType) {
        return metaModel.getPersister(entityType).getMetadata();
    }
}
