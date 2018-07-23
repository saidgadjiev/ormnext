package ru.saidgadjiev.ormnext.core.loader.object.collection;

import ru.saidgadjiev.ormnext.core.dao.Session;

import java.util.Set;

/**
 * Lazy set.
 *
 * @param <T> value type
 * @author Said Gadjiev
 */
public class LazySet<T> extends AbstractLazyCollection<T> implements Set<T> {

    /**
     * Create a new lazy set.
     *
     * @param collectionLoader collection loader
     * @param ownerId          owner id
     * @param set              original collection
     */
    public LazySet(CollectionLoader collectionLoader, Session session, Object ownerId, Set<T> set) {
        super(collectionLoader, session, ownerId, set);
    }
}
