package ru.saidgadjiev.ormnext.core.dao.transaction;

import ru.saidgadjiev.ormnext.core.support.DatabaseConnection;

import java.sql.SQLException;

public interface InternalTransaction extends Transaction {

    void changeState(TransactionState transactionState);

    void releaseConnection(DatabaseConnection<?> connection) throws SQLException;
}
