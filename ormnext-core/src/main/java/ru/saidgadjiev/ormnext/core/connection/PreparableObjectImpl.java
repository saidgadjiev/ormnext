package ru.saidgadjiev.ormnext.core.connection;

import java.sql.*;

/**
 * Preparable object for {@link PreparedStatement}.
 *
 * @author Said Gadjiev
 */
public class PreparableObjectImpl implements PreparableObject {

    /**
     * Prepared statement.
     */
    private final PreparedStatement preparedStatement;

    /**
     * Create a new instance.
     *
     * @param preparedStatement target prepared statement
     */
    public PreparableObjectImpl(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

    @Override
    public void setInt(int index, int value) throws SQLException {
        preparedStatement.setInt(index, value);
    }

    @Override
    public void setNull(int index, int sqlType) throws SQLException {
        preparedStatement.setNull(index, sqlType);
    }

    @Override
    public void setBoolean(int index, boolean value) throws SQLException {
        preparedStatement.setBoolean(index, value);
    }

    @Override
    public void setByte(int index, byte value) throws SQLException {
        preparedStatement.setByte(index, value);
    }

    @Override
    public void setDate(int index, Date value) throws SQLException {
        preparedStatement.setDate(index, value);
    }

    @Override
    public void setDouble(int index, double value) throws SQLException {
        preparedStatement.setDouble(index, value);
    }

    @Override
    public void setFloat(int index, float value) throws SQLException {
        preparedStatement.setFloat(index, value);
    }

    @Override
    public void setLong(int index, long value) throws SQLException {
        preparedStatement.setLong(index, value);
    }

    @Override
    public void setShort(int index, short value) throws SQLException {
        preparedStatement.setShort(index, value);
    }

    @Override
    public void setString(int index, String value) throws SQLException {
        preparedStatement.setString(index, value);
    }

    @Override
    public void setTime(int index, Time value) throws SQLException {
        preparedStatement.setTime(index, value);
    }

    @Override
    public void setTimestamp(int index, Timestamp value) throws SQLException {
        preparedStatement.setTimestamp(index, value);
    }

    @Override
    public <T> T getPreparedObject() {
        return (T) preparedStatement;
    }
}
