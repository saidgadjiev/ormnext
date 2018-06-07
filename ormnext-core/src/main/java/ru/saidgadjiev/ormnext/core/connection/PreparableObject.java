package ru.saidgadjiev.ormnext.core.connection;

import java.sql.*;

/**
 * Preparable object {@link ru.saidgadjiev.ormnext.core.field.datapersister.DataPersister#setObject}.
 *
 * @author Said Gadjiev
 */
public interface PreparableObject {

    /**
     * Set int.
     *
     * @param index target index
     * @param value target value
     * @throws SQLException any SQL exceptions
     */
    void setInt(int index, int value) throws SQLException;

    /**
     * Set sql null.
     *
     * @param index   target index
     * @param sqlType target sql type {@link Types}
     * @throws SQLException any SQL exceptions
     */
    void setNull(int index, int sqlType) throws SQLException;

    /**
     * Set boolean.
     *
     * @param index target index
     * @param value target value
     * @throws SQLException any SQL exceptions
     */
    void setBoolean(int index, boolean value) throws SQLException;

    /**
     * Set byte.
     *
     * @param index target index
     * @param value target value
     * @throws SQLException any SQL exceptions
     */
    void setByte(int index, byte value) throws SQLException;

    /**
     * Set date.
     *
     * @param index target index
     * @param value target value
     * @throws SQLException any SQL exceptions
     */
    void setDate(int index, Date value) throws SQLException;

    /**
     * Set double.
     *
     * @param index target index
     * @param value target value
     * @throws SQLException any SQL exceptions
     */
    void setDouble(int index, double value) throws SQLException;

    /**
     * Set float.
     *
     * @param index target index
     * @param value target value
     * @throws SQLException any SQL exceptions
     */
    void setFloat(int index, float value) throws SQLException;

    /**
     * Set long.
     *
     * @param index target index
     * @param value target value
     * @throws SQLException any SQL exceptions
     */
    void setLong(int index, long value) throws SQLException;

    /**
     * Set short.
     *
     * @param index target index
     * @param value target value
     * @throws SQLException any SQL exceptions
     */
    void setShort(int index, short value) throws SQLException;

    /**
     * Set string.
     *
     * @param index target index
     * @param value target value
     * @throws SQLException any SQL exceptions
     */
    void setString(int index, String value) throws SQLException;

    /**
     * Set time.
     *
     * @param index target index
     * @param value target value
     * @throws SQLException any SQL exceptions
     */
    void setTime(int index, Time value) throws SQLException;

    /**
     * Set timestamp.
     *
     * @param index target index
     * @param value target value
     * @throws SQLException any SQL exceptions
     */
    void setTimestamp(int index, Timestamp value) throws SQLException;

    /**
     * Return wrapped object.
     *
     * @param <T> wrapped object type
     * @return wrapped preparable object
     */
    <T> T getPreparedObject();
}
