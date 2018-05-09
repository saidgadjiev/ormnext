package ru.saidgadjiev.ormnext.core.dao;

import java.sql.SQLException;

public interface TransactionCallable<T> {

    T call() throws SQLException;
}
