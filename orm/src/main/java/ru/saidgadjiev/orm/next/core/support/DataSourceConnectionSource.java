package ru.saidgadjiev.orm.next.core.support;

import javax.sql.DataSource;
import java.sql.SQLException;

public class DataSourceConnectionSource implements ConnectionSource {

    private DataSource dataSource;

    public DataSourceConnectionSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DatabaseConnection getConnection() throws SQLException {
        return new SqlConnectionImpl(dataSource.getConnection());
    }
}
