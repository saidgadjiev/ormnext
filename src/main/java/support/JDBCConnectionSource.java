package support;

import db.dialect.IDialect;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by said on 25.02.17.
 */
public interface JDBCConnectionSource {

    Connection getConnection() throws SQLException;

    void releaseConnection(Connection connection) throws SQLException;

    IDialect getDialect();
}
