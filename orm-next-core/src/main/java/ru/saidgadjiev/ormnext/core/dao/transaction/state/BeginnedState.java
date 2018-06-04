package ru.saidgadjiev.ormnext.core.dao.transaction.state;

import ru.saidgadjiev.ormnext.core.connection.DatabaseConnection;

import java.sql.SQLException;

/**
 * Transaction began state.
 *
 * @author said gadjiev
 */
public class BeginnedState implements TransactionState {

    /**
     * Object which has transaction state.
     * @see InternalTransaction
     */
    private InternalTransaction transaction;

    /**
     * Create a new instance.
     * @param transaction target internal transaction
     */
    BeginnedState(InternalTransaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void begin(DatabaseConnection connection) throws SQLException {
        throw new SQLException("Transaction already beginTransaction");
    }

    @Override
    public void commit(DatabaseConnection connection) throws SQLException {
        try {
            connection.commit();
        } finally {
            connection.setAutoCommit(true);
        }

        transaction.changeState(new BeginState(transaction));
    }

    @Override
    public void rollback(DatabaseConnection connection) throws SQLException {
        connection.rollback();
        transaction.changeState(new BeginState(transaction));
    }
}
