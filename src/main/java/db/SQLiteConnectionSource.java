package db;

import db.dialect.IDialect;
import db.dialect.SQLiteDialect;
import support.JDBCConnectionSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by said on 25.02.17.
 */
public class SQLiteConnectionSource implements JDBCConnectionSource {

    private static final String DRIVER_NAME = "org.sqlite.JDBC";
    private final String URL;
    private final IDialect dialect;

    public SQLiteConnectionSource(String url) throws SQLException {
        this.URL = url;
        this.dialect = new SQLiteDialect();
        loadDriver();
    }

    protected void loadDriver() throws SQLException {
        try {
            Class.forName(DRIVER_NAME);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Missing driver " + DRIVER_NAME);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public void releaseConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public IDialect getDialect() {
        return dialect;
    }
}
