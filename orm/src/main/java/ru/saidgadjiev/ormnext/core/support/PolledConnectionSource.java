package ru.saidgadjiev.ormnext.core.support;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by said on 10.02.2018.
 */
public class PolledConnectionSource implements ConnectionSource<Connection> {

    private DataSource dataSource;

    private Set<DatabaseConnection<Connection>> available = new HashSet<>();

    private Set<DatabaseConnection<Connection>> inUse = new HashSet<>();

    public PolledConnectionSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public synchronized DatabaseConnection<Connection> getConnection() throws SQLException {
        if (available.isEmpty()) {
            available.add(createNew());
        }

        DatabaseConnection<Connection> connection = available.iterator().next();

        available.remove(connection);
        inUse.add(connection);

        return connection;
    }

    @Override
    public synchronized void releaseConnection(DatabaseConnection<Connection> connection) throws SQLException {
        inUse.remove(connection);
        available.add(connection);
    }

    private DatabaseConnection<Connection> createNew() throws SQLException {
        return new DatabaseConnectionImpl(dataSource.getConnection());
    }

    @Override
    public void close() throws SQLException {
        for (DatabaseConnection<Connection> connection: inUse) {
            connection.close();
        }
        for (DatabaseConnection<Connection> connection: available) {
            connection.close();
        }
    }

    @Override
    public String toString() {
        return String.format("Pool available=%d inUse=%d", available.size(), inUse.size());
    }
}
