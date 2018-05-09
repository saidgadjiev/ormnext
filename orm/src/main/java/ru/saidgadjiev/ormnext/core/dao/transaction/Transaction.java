package ru.saidgadjiev.ormnext.core.dao.transaction;

import ru.saidgadjiev.ormnext.core.dao.BaseDao;

import javax.transaction.TransactionRolledbackException;
import java.sql.SQLException;

public interface Transaction extends BaseDao {

    void beginTransaction() throws SQLException;

    void commit() throws SQLException;

    void rollback() throws SQLException;
}
