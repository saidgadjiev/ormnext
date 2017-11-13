package ru.said.miami.orm.core.query.core;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by said on 12.11.17.
 */
public class DatabaseResults implements AutoCloseable {

    private ResultSet resultSet;

    private ResultSetMetaData metaData;

    public DatabaseResults(ResultSet resultSet, ResultSetMetaData metaData) {
        this.resultSet = resultSet;
        this.metaData = metaData;

    }

    public boolean next() throws SQLException {
        return resultSet.next();
    }

    public Object getObject(int columnIndex) throws SQLException {
        return resultSet.getObject(columnIndex);
    }

    public Object getObject(String columnLabel) throws SQLException {
        return resultSet.getObject(columnLabel);
    }

    public void close() throws SQLException {
        resultSet.close();
    }
}
