package ru.saidgadjiev.ormnext.core.connection;

import java.sql.*;

public class OrmNextPreparedStatementImpl implements OrmNextPreparedStatement {

    private final PreparedStatement preparedStatement;

    public OrmNextPreparedStatementImpl(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

    @Override
    public void setInt(int index, int value) throws SQLException {
        preparedStatement.setInt(index, value);
    }

    @Override
    public void setNull(int index, int sqlType) {

    }

    @Override
    public void setBoolean(int index, boolean value) {

    }

    @Override
    public void setByte(int index, byte value) {

    }

    @Override
    public void setDate(int index, Date value) {

    }

    @Override
    public void setDouble(int index, double value) {

    }

    @Override
    public void setFloat(int index, float value) {

    }

    @Override
    public void setLong(int index, long value) {

    }

    @Override
    public void setShort(int index, short value) {

    }

    @Override
    public void setString(int index, String value) {

    }

    @Override
    public void setTime(int index, Time value) {

    }

    @Override
    public void setTimestamp(int index, Timestamp value) {

    }

    @Override
    public <T> T getPreparedObject() {
        return (T) preparedStatement;
    }

    @Override
    public void close() throws SQLException {
        preparedStatement.close();
    }
}
