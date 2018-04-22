package ru.saidgadjiev.orm.next.core.dao;

import ru.saidgadjiev.orm.next.core.stamentexecutor.CacheHelper;

import java.sql.SQLException;

/**
 * Created by said on 19.02.2018.
 */
public interface Session extends ExpandedDao, AutoCloseable {
    TransactionImpl transaction() throws SQLException;

    CacheHelper cacheHelper();

    void close();

    <T> CriteriaQuery<T> criteriaQuery(Class<T> persistenceClass);
}
