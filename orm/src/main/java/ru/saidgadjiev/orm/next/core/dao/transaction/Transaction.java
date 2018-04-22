package ru.saidgadjiev.orm.next.core.dao.transaction;

import ru.saidgadjiev.orm.next.core.dao.BaseDao;

import java.sql.SQLException;

public interface Transaction extends BaseDao {

    void begin() throws SQLException;

    void commit() throws SQLException;

    void rollback() throws SQLException;
}
