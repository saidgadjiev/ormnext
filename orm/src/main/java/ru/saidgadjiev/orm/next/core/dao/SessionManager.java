package ru.saidgadjiev.orm.next.core.dao;

import ru.saidgadjiev.orm.next.core.cache.ObjectCache;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;

/**
 * Класс для DAO
 */
public interface SessionManager {

    void caching(boolean flag, Class<?> ... classes);

    void setObjectCache(ObjectCache objectCache, Class<?> ... classes);

    ConnectionSource getDataSource();

    Session getSession();
}
