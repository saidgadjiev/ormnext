package ru.saidgadjiev.ormnext.core.dao.transaction.state;

import ru.saidgadjiev.ormnext.core.connectionsource.DatabaseConnection;

import java.sql.SQLException;

/**
 * Transaction state. Present State pattern.
 * Transaction will be in two states. Fisrt: transaction begin, second: beginned.
 * First state maen transaction was prepared for begin.
 * Second state mean transaction was prepared for commit or rollback.
 *
 * @author said gadjiev
 */
public interface TransactionState {

    /**
     * Transaction begin.
     * @param connection target connection
     * @throws SQLException any SQL exceptions
     */
    void begin(DatabaseConnection connection) throws SQLException;

    /**
     * Transaction commit.
     * @param connection target connection
     * @throws SQLException any SQL exceptions
     */
    void commit(DatabaseConnection connection) throws SQLException;

    /**
     * Transaction rollback.
     * @param connection target connection
     * @throws SQLException any SQL exceptions
     */
    void rollback(DatabaseConnection connection) throws SQLException;
}
