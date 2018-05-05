package ru.saidgadjiev.ormnext.core.dao.transaction;

import ru.saidgadjiev.ormnext.core.support.DatabaseConnection;

import java.sql.SQLException;

public interface TransactionState {

    void begin(DatabaseConnection<?> connection) throws SQLException;

    void commit(DatabaseConnection<?> connection) throws SQLException;

    void rollback(DatabaseConnection<?> connection) throws SQLException;
}
