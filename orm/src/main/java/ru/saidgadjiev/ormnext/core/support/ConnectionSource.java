package ru.saidgadjiev.ormnext.core.support;

import java.sql.SQLException;

public interface ConnectionSource<T> extends AutoCloseable {

    DatabaseConnection<T> getConnection() throws SQLException;

    default void releaseConnection(DatabaseConnection<T> connection) throws SQLException {
        connection.close();
    }

    default void close() throws SQLException {

    }
}
