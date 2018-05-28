package ru.saidgadjiev.ormnext.core.loader.object.collection;

import ru.saidgadjiev.ormnext.core.dao.SessionManager;

import java.util.Set;

/**
 * Lazy set.
 * @param <T> value type
 */
public class LazySet<T> extends AbstractLazyCollection<T> implements Set<T> {

    /**
     * Create a new lazy set.
     * @param collectionLoader collection loader
     * @param sessionManager session manager
     * @param ownerId owner id
     * @param set original collection
     */
    public LazySet(CollectionLoader collectionLoader, SessionManager sessionManager, Object ownerId, Set<T> set) {
        super(collectionLoader, sessionManager, ownerId, set);
    }
}
