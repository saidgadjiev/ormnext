package ru.saidgadjiev.ormnext.core.dao.transaction.state;

import ru.saidgadjiev.ormnext.core.connection_source.DatabaseConnection;

import java.sql.SQLException;

/**
 * Transaction prepared for begin state.
 */
public class BeginState implements TransactionState {

    /**
     * Object which has transaction state.
     * @see InternalTransaction
     */
    private InternalTransaction transaction;

    /**
     * Create a new instance.
     * @param transaction target internal transaction
     */
    public BeginState(InternalTransaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void begin(DatabaseConnection connection) throws SQLException {
        connection.setAutoCommit(false);
        transaction.changeState(new BeginnedState(transaction));
    }

    @Override
    public void commit(DatabaseConnection connection) throws SQLException {
        throw new SQLException("Transaction not beginTransaction");
    }

    @Override
    public void rollback(DatabaseConnection connection) throws SQLException {
        throw new SQLException("Transaction not beginTransaction");
    }
}
