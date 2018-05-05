package ru.saidgadjiev.ormnext.core.support;

import java.sql.SQLException;

/**
 * Created by said on 10.03.2018.
 */
public class WrappedConnectionSource implements ConnectionSource {

    private ConnectionSource connectionSource;

    public WrappedConnectionSource(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
    }

    @Override
    public DatabaseConnection getConnection() throws SQLException {
        return connectionSource.getConnection();
    }

    @Override
    public void releaseConnection(DatabaseConnection connection) throws SQLException {
        connectionSource.releaseConnection(connection);
    }

    @Override
    public void close() throws SQLException {
        connectionSource.close();
    }
}
