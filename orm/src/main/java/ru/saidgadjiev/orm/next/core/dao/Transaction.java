package ru.saidgadjiev.orm.next.core.dao;

import java.sql.SQLException;

public interface Transaction<T, ID> extends BaseDao<T, ID>, AutoCloseable {

    void begin() throws SQLException;

    void commit() throws SQLException;

    void rollback() throws SQLException;
}
