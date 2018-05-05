package ru.saidgadjiev.ormnext.core.stamentexecutor.object.collection;

import ru.saidgadjiev.ormnext.core.dao.Dao;
import ru.saidgadjiev.ormnext.core.stamentexecutor.rowreader.entityinitializer.CollectionLoader;
import ru.saidgadjiev.ormnext.core.dao.Dao;
import ru.saidgadjiev.ormnext.core.stamentexecutor.rowreader.entityinitializer.CollectionLoader;

import java.util.Set;

/**
 * Created by said on 09.03.2018.
 */
public class LazySet<T> extends AbstractLazyCollection<T> implements Set<T> {

    public LazySet(CollectionLoader collectionLoader, Dao dao, Object ownerId, Set<T> set) {
        super(collectionLoader, dao, ownerId, set);
    }
}
