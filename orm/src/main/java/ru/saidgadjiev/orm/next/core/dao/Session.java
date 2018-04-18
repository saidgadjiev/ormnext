package ru.saidgadjiev.orm.next.core.dao;

import java.io.Closeable;
import java.sql.SQLException;

/**
 * Created by said on 19.02.2018.
 */
public interface Session extends BaseDao, AutoCloseable {
    TransactionImpl transaction() throws SQLException;

    void addEntityToCache(Class<?> clazz, Object id, Object data);

    void close();
}
