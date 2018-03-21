package ru.saidgadjiev.orm.next.core.support;

import ru.saidgadjiev.orm.next.core.db.DatabaseType;

import java.sql.Connection;
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
    public Connection getConnection() throws SQLException {
        return connectionSource.getConnection();
    }

    @Override
    public void releaseConnection(Connection connection) throws SQLException {
        connectionSource.releaseConnection(connection);
    }

    @Override
    public DatabaseType getDatabaseType() {
        return connectionSource.getDatabaseType();
    }

    @Override
    public void close() throws Exception {
        connectionSource.close();
    }
}
