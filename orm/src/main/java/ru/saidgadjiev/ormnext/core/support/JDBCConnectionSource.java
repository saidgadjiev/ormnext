package ru.saidgadjiev.ormnext.core.support;

import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCConnectionSource implements ConnectionSource {

    private final String dataBaseUrl;

    private String driverClassName;


    public JDBCConnectionSource(String dataBaseUrl, String driverClassName) throws SQLException {
        this.dataBaseUrl = dataBaseUrl;
        this.driverClassName = driverClassName;
        initialize();
    }

    private void initialize() throws SQLException {
        loadDriver();
    }

    private void loadDriver() throws SQLException {
        if (driverClassName != null) {
            try {
                Class.forName(driverClassName);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver class was not found.  Missing jar with class " + driverClassName + ".", e);
            }
        }
    }

    @Override
    public DatabaseConnection getConnection() throws SQLException {
        return new SqlConnectionImpl(DriverManager.getConnection(dataBaseUrl));
    }

}
