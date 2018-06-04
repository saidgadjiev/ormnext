package ru.saidgadjiev.ormnext.core.connection;

import java.sql.*;

public interface OrmNextPreparedStatement extends AutoCloseable {

    void setInt(int index, int value) throws SQLException;

    void close() throws SQLException;

    void setNull(int index, int sqlType);

    void setBoolean(int index, boolean value);

    void setByte(int index, byte value);

    void setDate(int index, Date value);

    void setDouble(int index, double value);

    void setFloat(int index, float value);

    void setLong(int index, long value);

    void setShort(int index, short value);

    void setString(int index, String value);

    void setTime(int index, Time value);

    void setTimestamp(int index, Timestamp value);

    <T> T getPreparedObject();
}
