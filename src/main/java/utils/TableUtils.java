package utils;

import support.JDBCConnectionSource;
import table.DBTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by said on 25.02.17.
 */
public class TableUtils {

    private TableUtils() {
    }

    public static<T> void createTable(JDBCConnectionSource connectionSource, Class<T> dbTable) throws SQLException {
        String dbTableName = dbTable.getAnnotation(DBTable.class).name();
        StringBuilder sbCreateTable = new StringBuilder();

        sbCreateTable.append("CREATE TABLE ").append(dbTableName).append("");
        Statement statement = null;
        Connection connection = connectionSource.getConnection();

        try {
            statement = connection.createStatement();
            statement.execute("CREATE TABLE t(x INTEGER PRIMARY KEY ASC, y, z);");
        } finally {
            if (statement != null) {
                statement.close();
            }
            connectionSource.releaseConnection(connection);
        }
    }
}
