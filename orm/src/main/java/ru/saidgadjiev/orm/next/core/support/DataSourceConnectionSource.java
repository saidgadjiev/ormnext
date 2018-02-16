package ru.saidgadjiev.orm.next.core.support;

import ru.saidgadjiev.orm.next.core.db.DatabaseType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceConnectionSource implements ConnectionSource {

    private DataSource dataSource;

    private DatabaseType databaseType;

    public DataSourceConnectionSource(DataSource dataSource, DatabaseType databaseType) {
        this.dataSource = dataSource;
        this.databaseType = databaseType;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public DatabaseType getDatabaseType() {
        return databaseType;
    }
}