package ru.saidgadjiev.ormnext.core.connection;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Provide method for retrieve results from database result set.
 *
 * @author said gadjiev
 */
public interface DatabaseResults extends AutoCloseable {

    /**
     * True if result set has next row.
     *
     * @return true if result set has next row
     * @throws SQLException any SQL exceptions
     */
    boolean next() throws SQLException;

    /**
     * Retrieve string value from result set by column number.
     *
     * @param columnId target column number
     * @return retrieved string value
     * @throws SQLException any SQL exceptions
     */
    String getString(int columnId) throws SQLException;

    /**
     * Retrieve string value from result set by column name.
     *
     * @param columnLabel target column name
     * @return retrieved string value
     * @throws SQLException any SQL exceptions
     */
    String getString(String columnLabel) throws SQLException;

    /**
     * Retrieve int value from result set by column number.
     *
     * @param columnId target column number
     * @return retrieved int value
     * @throws SQLException any SQL exceptions
     */
    int getInt(int columnId) throws SQLException;

    /**
     * Retrieve int value from result set by column name.
     *
     * @param columnLabel target column name
     * @return retrieved int value
     * @throws SQLException any SQL exceptions
     */
    int getInt(String columnLabel) throws SQLException;

    /**
     * Retrieve boolean value from result set by column number.
     *
     * @param columnId target column number
     * @return retrieved boolean value
     * @throws SQLException any SQL exceptions
     */
    boolean getBoolean(int columnId) throws SQLException;

    /**
     * Retrieve boolean value from result set by column name.
     *
     * @param columnLabel target column name
     * @return retrieved boolean value
     * @throws SQLException any SQL exceptions
     */
    boolean getBoolean(String columnLabel) throws SQLException;

    /**
     * Retrieve double value from result set by column number.
     *
     * @param columnId target column number
     * @return retrieved double value
     * @throws SQLException any SQL exceptions
     */
    double getDouble(int columnId) throws SQLException;

    /**
     * Retrieve double value from result set by column name.
     *
     * @param columnLabel target column name
     * @return retrieved double value
     * @throws SQLException any SQL exceptions
     */
    double getDouble(String columnLabel) throws SQLException;

    /**
     * Retrieve float value from result set by column number.
     *
     * @param columnId target column number
     * @return retrieved float value
     * @throws SQLException any SQL exceptions
     */
    float getFloat(int columnId) throws SQLException;

    /**
     * Retrieve float value from result set by column name.
     *
     * @param columnLabel target column name
     * @return retrieved float value
     * @throws SQLException any SQL exceptions
     */
    float getFloat(String columnLabel) throws SQLException;

    /**
     * Retrieve long value from result set by column number.
     *
     * @param columnId target column number
     * @return retrieved long value
     * @throws SQLException any SQL exceptions
     */
    Long getLong(int columnId) throws SQLException;

    /**
     * Retrieve long value from result set by column name.
     *
     * @param columnLabel target column name
     * @return retrieved long value
     * @throws SQLException any SQL exceptions
     */
    Long getLong(String columnLabel) throws SQLException;

    /**
     * Retrieve byte value from result set by column name.
     *
     * @param columnLabel target column name
     * @return retrieved byte value
     * @throws SQLException any SQL exceptions
     */
    byte getByte(String columnLabel) throws SQLException;

    /**
     * Retrieve byte value from result set by column number.
     *
     * @param columnId target column number
     * @return retrieved byte value
     * @throws SQLException any SQL exceptions
     */
    byte getByte(int columnId) throws SQLException;

    /**
     * Retrieve short value from result set by column name.
     *
     * @param columnLabel target column name
     * @return retrieved short value
     * @throws SQLException any SQL exceptions
     */
    short getShort(String columnLabel) throws SQLException;

    /**
     * Retrieve short value from result set by column number.
     *
     * @param columnId target column number
     * @return retrieved short value
     * @throws SQLException any SQL exceptions
     */
    short getShort(int columnId) throws SQLException;

    /**
     * Retrieve date {@link Date} value from result set by column name.
     *
     * @param columnLabel target column name
     * @return retrieved date value
     * @throws SQLException any SQL exceptions
     */
    Date getDate(String columnLabel) throws SQLException;

    /**
     * Retrieve date {@link Date} value from result set by column number.
     *
     * @param columnId target column number
     * @return retrieved date value
     * @throws SQLException any SQL exceptions
     */
    Date getDate(int columnId) throws SQLException;

    /**
     * Retrieve time {@link Time} value from result set by column number.
     *
     * @param columnId target column number
     * @return retrieved time value
     * @throws SQLException any SQL exceptions
     */
    Time getTime(int columnId) throws SQLException;

    /**
     * Retrieve time {@link Time} value from result set by column name.
     *
     * @param columnLabel target column name
     * @return retrieved time value
     * @throws SQLException any SQL exceptions
     */
    Time getTime(String columnLabel) throws SQLException;

    /**
     * Retrieve timestamp {@link Timestamp} value from result set by column name.
     *
     * @param columnLabel target column number
     * @return retrieved timestamp value
     * @throws SQLException any SQL exceptions
     */
    Timestamp getTimestamp(String columnLabel) throws SQLException;

    /**
     * Retrieve timestamp {@link Timestamp} value from result set by column number.
     *
     * @param columnId target column number
     * @return retrieved timestamp value
     * @throws SQLException any SQL exceptions
     */
    Timestamp getTimestamp(int columnId) throws SQLException;

    /**
     * Return true if last read value from result set was sql null.
     *
     * @return true if last read value from result set was sql null
     * @throws SQLException any SQL exceptions
     */
    boolean wasNull() throws SQLException;

    /**
     * Wrapped result set object.
     * @param <T> result set type
     * @return result set object
     */
    <T> T getResultsObject();

    /**
     * Close result set resources.
     * @throws SQLException any SQL exceptions
     */
    void close() throws SQLException;
}
