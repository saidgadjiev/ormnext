package ru.saidgadjiev.orm.next.core.dao;

import ru.saidgadjiev.orm.next.core.db.DatabaseType;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by said on 28.01.2018.
 */
public class WrappedConnectionSource implements ConnectionSource, AutoCloseable {

    private ConnectionSource connectionSource;

    private Connection connection;

    public WrappedConnectionSource(ConnectionSource connectionSource) throws SQLException {
        this.connectionSource = connectionSource;
        this.connection = connectionSource.getConnection();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public DatabaseType getDatabaseType() {
        return connectionSource.getDatabaseType();
    }

    @Override
    public void releaseConnection(Connection connection) throws SQLException {
    }

    @Override
    public void close() throws Exception {
        connectionSource.releaseConnection(connection);
    }
}
