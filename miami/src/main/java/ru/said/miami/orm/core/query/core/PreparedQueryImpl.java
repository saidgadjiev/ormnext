package ru.said.miami.orm.core.query.core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by said on 12.11.17.
 */
public class PreparedQueryImpl implements AutoCloseable {

    private PreparedStatement preparedStatement;

    public PreparedQueryImpl(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

    public void setObject(int index, Object value) throws SQLException {
        preparedStatement.setObject(index, value);
    }

    public DatabaseResults executeQuery() throws SQLException {
        ResultSet resultSet = preparedStatement.executeQuery();

        return new DatabaseResults(resultSet, resultSet.getMetaData());
    }

    public int executeUpdate() throws SQLException {
        return preparedStatement.executeUpdate();
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        return preparedStatement.getGeneratedKeys();
    }

    public int getUpdateCount() throws SQLException {
        return preparedStatement.getUpdateCount();
    }

    public void close() throws SQLException {
        preparedStatement.close();
    }
}
