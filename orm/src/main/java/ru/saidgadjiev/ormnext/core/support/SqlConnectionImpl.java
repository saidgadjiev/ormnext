package ru.saidgadjiev.ormnext.core.support;

import java.sql.Connection;
import java.sql.SQLException;

public class SqlConnectionImpl extends DatabaseConnection<Connection> {

    public SqlConnectionImpl(Connection connection) {
        super(connection);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
    }

    @Override
    public void commit() throws SQLException {
        connection.commit();
    }

    @Override
    public void rollback() throws SQLException {
        connection.rollback();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return connection.isClosed();
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }
}
