package ru.saidgadjiev.ormnext.core.dao.transaction;

public interface InternalTransaction extends Transaction {

    void changeState(TransactionState transactionState);
}
