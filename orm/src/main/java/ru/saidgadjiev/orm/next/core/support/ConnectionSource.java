package ru.saidgadjiev.orm.next.core.support;

import ru.saidgadjiev.orm.next.core.db.DatabaseType;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionSource extends AutoCloseable {

    Connection getConnection() throws SQLException;

    default void releaseConnection(Connection connection) throws SQLException {
        connection.close();
    }

    DatabaseType getDatabaseType();

    default void close() throws Exception {

    }
}
