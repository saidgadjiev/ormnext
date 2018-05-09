package ru.saidgadjiev.ormnext.core.support;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

public interface DatabaseResultSet extends AutoCloseable {

    boolean next() throws SQLException;

    String getString(int columnId) throws SQLException;

    String getString(String columnLabel) throws SQLException;

    int getInt(int columnId) throws SQLException;

    int getInt(String columnLabel) throws SQLException;

    boolean getBoolean(int columnId) throws SQLException;

    boolean getBoolean(String columnLabel) throws SQLException;

    double getDouble(int columnId) throws SQLException;

    double getDouble(String columnLabel) throws SQLException;

    float getFloat(int columnId) throws SQLException;

    float getFloat(String columnLabel) throws SQLException;

    Long getLong(int columnId) throws SQLException;

    Long getLong(String columnLabel) throws SQLException;

    int getColumnType(int columnId) throws SQLException;

    void close() throws SQLException;

    byte getByte(String columnLabel) throws SQLException;

    byte getByte(int columnId) throws SQLException;

    short getShort(String columnLabel) throws SQLException;

    short getShort(int columnId) throws SQLException;

    Date getDate(String columnLabel) throws SQLException;

    Date getDate(int column) throws SQLException;

    Time getTime(int column) throws SQLException;

    Time getTime(String columnLabel) throws SQLException;

    Timestamp getTimestamp(String columnLabel) throws SQLException;

    Timestamp getTimestamp(int column) throws SQLException;

    boolean wasNull() throws SQLException;
}
