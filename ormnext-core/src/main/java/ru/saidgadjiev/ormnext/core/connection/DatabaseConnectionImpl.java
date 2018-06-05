package ru.saidgadjiev.ormnext.core.connection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Implementation for jdbc connection {@link Connection}.
 */
public class DatabaseConnectionImpl extends DatabaseConnection<Connection> {

    /**
     * Create a new connection.
     * @param connection target physical connection
     */
    public DatabaseConnectionImpl(Connection connection) {
        super(connection);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        getConnection().setAutoCommit(autoCommit);
    }

    @Override
    public void commit() throws SQLException {
        getConnection().commit();
    }

    @Override
    public void rollback() throws SQLException {
        getConnection().rollback();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return getConnection().isClosed();
    }

    @Override
    public void close() throws SQLException {
        getConnection().close();
    }
}
