package ru.saidgadjiev.orm.next.core.dao;

import ru.saidgadjiev.orm.next.core.cache.ObjectCache;
import ru.saidgadjiev.orm.next.core.stamentexecutor.CacheHelper;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.table.internal.metamodel.MetaModel;

import java.sql.SQLException;

/**
 * Created by said on 19.02.2018.
 */

//TODO: переименовать в Dao так как Dao как таковой нету тут. Поставить размер кеша для сессии 32
public interface Dao extends AutoCloseable, BaseDao {

    TransactionImpl transaction() throws SQLException;

    CacheHelper cacheHelper();

    void setObjectCache(ObjectCache objectCache, Class<?> ... classes);

    ConnectionSource getDataSource();

    MetaModel getMetaModel();

    DatabaseEngine getDatabaseEngine();

    void close() throws SQLException;
}
