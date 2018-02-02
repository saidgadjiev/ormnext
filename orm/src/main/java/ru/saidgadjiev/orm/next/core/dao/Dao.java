package ru.saidgadjiev.orm.next.core.dao;

import ru.saidgadjiev.orm.next.core.cache.ObjectCache;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;

import java.sql.SQLException;

/**
 * Класс для DAO
 * @param <T> тип объекта
 * @param <ID> тип id
 */
public interface Dao<T, ID> extends BaseDao<T, ID> {

    void caching(boolean flag);

    void setObjectCache(ObjectCache objectCache);

    void caching(boolean flag, ObjectCache objectCache);

    ConnectionSource getDataSource();

    TransactionImpl<T, ID> transaction() throws SQLException;
}
