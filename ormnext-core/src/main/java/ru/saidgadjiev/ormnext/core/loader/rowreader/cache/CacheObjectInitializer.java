package ru.saidgadjiev.ormnext.core.loader.rowreader.cache;

import java.sql.SQLException;

/**
 * Initialize cached object.
 *
 * @author Said Gadjiev
 */

public class CacheObjectInitializer {

    /**
     * Initialize cached object.
     *
     * @param cacheObjectContext target context
     * @param object target object
     * @throws SQLException any exceptions
     */
    public void initialize(CacheObjectContext cacheObjectContext, Object object) throws SQLException {
        cacheObjectContext.getMetadata(object.getClass()).accept(new LoadFromCache(cacheObjectContext, object));
    }
}
