package ru.saidgadjiev.orm.next.core.support;

import ru.saidgadjiev.orm.next.core.db.DatabaseType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCConnectionSource implements ConnectionSource {

    private final String dataBaseUrl;

    private final DatabaseType databaseType;


    public JDBCConnectionSource(String dataBaseUrl, DatabaseType databaseType) throws SQLException {
        this.dataBaseUrl = dataBaseUrl;
        this.databaseType = databaseType;
        initialize();
    }

    private void initialize() throws SQLException {
        databaseType.loadDriver();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dataBaseUrl);
    }

    @Override
    public DatabaseType getDatabaseType() {
        return databaseType;
    }
}
