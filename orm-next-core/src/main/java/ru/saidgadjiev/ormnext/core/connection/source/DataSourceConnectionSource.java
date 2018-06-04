package ru.saidgadjiev.ormnext.core.connection.source;

import ru.saidgadjiev.ormnext.core.connection.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.connection.DatabaseConnectionImpl;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Connection provider from datasource {@link DataSource}.
 */
public class DataSourceConnectionSource implements ConnectionSource<Connection> {

    /**
     * Datasource.
     */
    private DataSource dataSource;

    /**
     * Create a new connection source.
     * @param dataSource target datasource
     */
    public DataSourceConnectionSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public DatabaseConnection<Connection> getConnection() throws SQLException {
        return new DatabaseConnectionImpl(dataSource.getConnection());
    }
}
