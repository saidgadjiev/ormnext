package ru.saidgadjiev.orm.next.core.support;

import ru.saidgadjiev.orm.next.core.db.DatabaseType;

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

    private DatabaseType databaseType;

    private Set<Connection> available = new HashSet<>();

    private Set<Connection> inUse = new HashSet<>();

    public PolledConnectionSource(DataSource dataSource, DatabaseType databaseType) {
        this.dataSource = dataSource;
        this.databaseType = databaseType;
    }

    @Override
    public synchronized Connection getConnection() throws SQLException {
        if (available.isEmpty()) {
            available.add(createNew());
        }

        Connection connection = available.iterator().next();

        available.remove(connection);
        inUse.add(connection);

        return connection;
    }

    @Override
    public synchronized void releaseConnection(Connection connection) throws SQLException {
        inUse.remove(connection);
        available.add(connection);
    }

    @Override
    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    private Connection createNew() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void close() throws Exception {
        for (Connection connection: inUse) {
            connection.close();
        }
        for (Connection connection: available) {
            connection.close();
        }
    }

    @Override
    public String toString() {
        return String.format("Pool available=%d inUse=%d", available.size(), inUse.size());
    }
}
