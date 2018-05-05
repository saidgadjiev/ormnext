package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.cache.ObjectCache;
import ru.saidgadjiev.ormnext.core.support.ConnectionSource;

import java.sql.SQLException;

/**
 * Created by said on 19.02.2018.
 */

//TODO: переименовать в Dao так как Dao как таковой нету тут. Поставить размер кеша для сессии 32
public interface Dao extends AutoCloseable, BaseDao {

    TransactionImpl transaction() throws SQLException;

    void enableDefaultCache();

    void setObjectCache(ObjectCache objectCache);

    ConnectionSource getDataSource();

    void close() throws SQLException;
}
