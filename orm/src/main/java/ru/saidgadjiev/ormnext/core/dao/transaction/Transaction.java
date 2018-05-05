package ru.saidgadjiev.ormnext.core.dao.transaction;

import ru.saidgadjiev.ormnext.core.dao.BaseDao;

import java.sql.SQLException;

public interface Transaction extends BaseDao {

    void begin() throws SQLException;

    void commit() throws SQLException;

    void rollback() throws SQLException;
}
