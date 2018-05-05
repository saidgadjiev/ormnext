package ru.saidgadjiev.ormnext.core.support;

import java.sql.SQLException;

public interface ConnectionSource extends AutoCloseable {

    DatabaseConnection getConnection() throws SQLException;

    default void releaseConnection(DatabaseConnection connection) throws SQLException {
        connection.close();
    }

    default void close() throws SQLException {

    }
}
