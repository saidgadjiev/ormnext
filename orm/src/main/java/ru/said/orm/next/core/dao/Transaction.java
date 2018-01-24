package ru.said.orm.next.core.dao;

import java.sql.SQLException;

public interface Transaction<T, ID> extends Dao<T, ID>, AutoCloseable {

    void beginTrans() throws SQLException;

    void commitTrans() throws SQLException;

    void rollback() throws SQLException;
}
