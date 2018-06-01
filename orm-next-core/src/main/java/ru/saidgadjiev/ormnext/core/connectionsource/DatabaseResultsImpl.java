package ru.saidgadjiev.ormnext.core.connectionsource;

import java.sql.*;

/**
 * Database result set implementation for retrieve results from {@link ResultSet}.
 * It delegate close method.
 *
 * @author said gadjiev
 */
public abstract class DatabaseResultsImpl implements DatabaseResults {

    /**
     * Result set object.
     */
    private ResultSet resultSet;

    /**
     * Create a new instance.
     * @param resultSet target result set
     */
    public DatabaseResultsImpl(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public boolean next() throws SQLException {
        return resultSet.next();
    }

    @Override
    public String getString(int columnId) throws SQLException {
        return resultSet.getString(columnId);
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        return resultSet.getString(columnLabel);
    }

    @Override
    public int getInt(int columnId) throws SQLException {
        return resultSet.getInt(columnId);
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        return resultSet.getInt(columnLabel);
    }

    @Override
    public boolean getBoolean(int columnId) throws SQLException {
        return resultSet.getBoolean(columnId);
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        return resultSet.getBoolean(columnLabel);
    }

    @Override
    public double getDouble(int columnId) throws SQLException {
        return resultSet.getDouble(columnId);
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        return resultSet.getDouble(columnLabel);
    }

    @Override
    public float getFloat(int columnId) throws SQLException {
        return resultSet.getFloat(columnId);
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        return resultSet.getFloat(columnLabel);
    }

    @Override
    public Long getLong(int columnId) throws SQLException {
        return resultSet.getLong(columnId);
    }

    @Override
    public Long getLong(String columnLabel) throws SQLException {
        return resultSet.getLong(columnLabel);
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        return resultSet.getByte(columnLabel);
    }

    @Override
    public byte getByte(int columnId) throws SQLException {
        return resultSet.getByte(columnId);
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        return resultSet.getShort(columnLabel);
    }

    @Override
    public short getShort(int columnId) throws SQLException {
        return resultSet.getShort(columnId);
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        return resultSet.getDate(columnLabel);
    }

    @Override
    public Date getDate(int column) throws SQLException {
        return resultSet.getDate(column);
    }

    @Override
    public Time getTime(int column) throws SQLException {
        return resultSet.getTime(column);
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        return resultSet.getTime(columnLabel);
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return resultSet.getTimestamp(columnLabel);
    }

    @Override
    public Timestamp getTimestamp(int column) throws SQLException {
        return resultSet.getTimestamp(column);
    }

    @Override
    public boolean wasNull() throws SQLException {
        return resultSet.wasNull();
    }

    @Override
    public <T> T getResultsObject() {
        return (T) resultSet;
    }
}
