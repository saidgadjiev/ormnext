package ru.saidgadjiev.ormnext.core.dao.transaction;

import ru.saidgadjiev.ormnext.core.support.DatabaseConnection;

import java.sql.SQLException;

public class BeginnedState implements TransactionState {

    private InternalTransaction transaction;

    BeginnedState(InternalTransaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void begin(DatabaseConnection<?> connection) throws SQLException {
        throw new SQLException("Transaction already begin");
    }

    @Override
    public void commit(DatabaseConnection<?> connection) throws SQLException {
        try {
            connection.commit();
        } finally {
            connection.setAutoCommit(true);
        }

        transaction.releaseConnection(connection);
        transaction.changeState(new BeginState(transaction));
    }

    @Override
    public void rollback(DatabaseConnection<?> connection) throws SQLException {
        connection.rollback();
        transaction.releaseConnection(connection);
        transaction.changeState(new BeginState(transaction));
    }
}
