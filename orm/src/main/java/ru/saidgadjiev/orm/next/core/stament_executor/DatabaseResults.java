package ru.saidgadjiev.orm.next.core.stament_executor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 12.11.17.
 */
@SuppressWarnings("PMD")
public class DatabaseResults implements AutoCloseable {

    private ResultSet resultSet;

    private ResultSetMetaData metaData;

    public DatabaseResults(ResultSet resultSet, ResultSetMetaData metaData) {
        this.resultSet = resultSet;
        this.metaData = metaData;
    }

    public List<String> getColumnNames() throws SQLException {
        int count = metaData.getColumnCount();
        List<String> columns = new ArrayList<>();

        for (int i = 0; i < count; ++i) {
            columns.add(metaData.getColumnName(i));
        }

        return columns;
    }

    public boolean hasColumn(String name) throws SQLException {
        int count = metaData.getColumnCount();

        for (int i = 0; i < count; ++i) {
            if (name.equals(metaData.getColumnName(i))) {
                return true;
            }
        }

        return false;
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

    public long getLong(int columnIndex) throws SQLException {
        return resultSet.getLong(columnIndex);
    }

    public long getLong(String columnLabel) throws SQLException {
        return resultSet.getLong(columnLabel);
    }

    public void close() throws SQLException {
        resultSet.close();
    }
}
