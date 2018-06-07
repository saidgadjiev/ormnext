package ru.saidgadjiev.ormnext.core.field.datapersister;

import java.sql.SQLException;

/**
 * Convert java object to sql value and sql value to java value.
 *
 * @param <S> sql value type
 * @param <J> java value type
 * @author Said Gadjiev
 */
public interface ColumnConverter<S, J> {

    /**
     * Java object value to sql value.
     *
     * @param value target value
     * @return converted value
     * @throws SQLException any SQL exceptions
     */
    S javaToSql(J value) throws SQLException;

    /**
     * SQL value to java.
     *
     * @param value target value
     * @return converted value
     * @throws SQLException any SQL exceptions
     */
    J sqlToJava(S value) throws SQLException;
}
