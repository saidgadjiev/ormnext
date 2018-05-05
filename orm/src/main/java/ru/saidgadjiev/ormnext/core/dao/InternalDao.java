package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.stamentexecutor.CacheHelper;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;

public interface InternalDao extends Dao {

    Object createProxy(Class<?> entityClass, Object id);

    CacheHelper cacheHelper();

    MetaModel getMetaModel();

    DatabaseEngine getDatabaseEngine();
}
