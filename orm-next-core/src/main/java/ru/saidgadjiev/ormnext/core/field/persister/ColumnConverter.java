package ru.saidgadjiev.ormnext.core.field.persister;

import java.sql.SQLException;

/**
 * Convert java object to sql value and sql value to java value.
 * @param <T> sql value type
 * @param <R> java value type
 */
public interface ColumnConverter<T, R> {

    /**
     * Java object value to sql value.
     * @param value target value
     * @return converted value
     * @throws SQLException any SQL exceptions
     */
    T javaToSql(R value) throws SQLException;

    /**
     * SQL value to java.
     * @param value target value
     * @return converted value
     * @throws SQLException any SQL exceptions
     */
    R sqlToJava(T value) throws SQLException;
}
