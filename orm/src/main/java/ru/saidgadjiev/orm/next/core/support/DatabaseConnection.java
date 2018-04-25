package ru.saidgadjiev.orm.next.core.support;

import java.sql.SQLException;

public abstract class DatabaseConnection<T> {

    protected final T connection;

    public DatabaseConnection(T connection) {
        this.connection = connection;
    }

    public T getConnection() {
        return (T) connection;
    }

    public abstract void setAutoCommit(boolean autoCommit) throws SQLException;

    public abstract void commit() throws SQLException;

    public abstract void rollback() throws SQLException;

    public abstract boolean isClosed() throws SQLException;

    public abstract void close() throws SQLException;
}
