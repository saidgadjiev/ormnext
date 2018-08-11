package ru.saidgadjiev.ormnext.core.dao.transaction.state;

import ru.saidgadjiev.ormnext.core.connection.DatabaseConnection;

import java.sql.SQLException;

/**
 * Transaction began state.
 *
 * @author Said Gadjiev
 */
public class BeginnedState implements TransactionState {

    /**
     * Object which has transaction state.
     * @see SessionTransactionContract
     */
    private SessionTransactionContract transaction;

    /**
     * Create a new instance.
     * @param transaction target internal transaction
     */
    BeginnedState(SessionTransactionContract transaction) {
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
