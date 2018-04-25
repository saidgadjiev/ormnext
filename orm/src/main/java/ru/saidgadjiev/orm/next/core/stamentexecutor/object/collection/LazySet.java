package ru.saidgadjiev.orm.next.core.stamentexecutor.object.collection;

import ru.saidgadjiev.orm.next.core.dao.Dao;
import ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader.entityinitializer.CollectionLoader;

import java.util.Set;

/**
 * Created by said on 09.03.2018.
 */
public class LazySet<T> extends AbstractLazyCollection<T> implements Set<T> {

    public LazySet(CollectionLoader collectionLoader, Dao dao, Object ownerId, Set<T> set) {
        super(collectionLoader, dao, ownerId, set);
    }
}
