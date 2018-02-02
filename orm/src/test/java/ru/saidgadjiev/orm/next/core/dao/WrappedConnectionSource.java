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

    public WrappedConnectionSource(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = connectionSource.getConnection();
        }

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
