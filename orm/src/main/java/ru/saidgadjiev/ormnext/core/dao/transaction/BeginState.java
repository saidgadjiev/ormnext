package ru.saidgadjiev.ormnext.core.dao.transaction;

import ru.saidgadjiev.ormnext.core.support.DatabaseConnection;

import java.sql.SQLException;

public class BeginState implements TransactionState {

    private InternalTransaction transaction;

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
