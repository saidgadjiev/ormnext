package ru.saidgadjiev.orm.next.core.dao;

import java.sql.SQLException;

public interface Transaction extends BaseDao, AutoCloseable {

    void begin() throws SQLException;

    void commit() throws SQLException;

    void rollback() throws SQLException;
}
