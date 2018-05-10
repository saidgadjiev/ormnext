package ru.saidgadjiev.orm.next.core.support;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by said on 10.02.2018.
 */
public class PolledConnectionSource implements ConnectionSource {

    private DataSource dataSource;

    private Set<DatabaseConnection> available = new HashSet<>();

    private Set<DatabaseConnection> inUse = new HashSet<>();

    public PolledConnectionSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public synchronized DatabaseConnection getConnection() throws SQLException {
        if (available.isEmpty()) {
            available.add(new SqlConnectionImpl(createNew()));
        }

        DatabaseConnection connection = available.iterator().next();

        available.remove(connection);
        inUse.add(connection);

        return connection;
    }

    @Override
    public synchronized void releaseConnection(DatabaseConnection connection) throws SQLException {
        inUse.remove(connection);
        available.add(connection);
    }

    private Connection createNew() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void close() throws SQLException {
        for (DatabaseConnection connection: inUse) {
            connection.close();
        }
        for (DatabaseConnection connection: available) {
            connection.close();
        }
    }

    @Override
    public String toString() {
        return String.format("Pool available=%d inUse=%d", available.size(), inUse.size());
    }
}
