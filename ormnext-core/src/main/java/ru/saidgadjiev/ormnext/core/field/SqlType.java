package ru.saidgadjiev.ormnext.core.field;

/**
 * Supported sql types.
 */
public enum SqlType {

    /**
     * Boolean {@link Boolean}.
     */
    BOOLEAN,

    /**
     * Byte {@link Byte}.
     */
    BYTE,

    /**
     * Date {@link java.sql.Date}.
     */
    DATE,

    /**
     * Double {@link Double}.
     */
    DOUBLE,

    /**
     * Float {@link Float}.
     */
    FLOAT,

    /**
     * Integer {@link Integer}.
     */
    INTEGER,

    /**
     * Long {@link Long}.
     */
    LONG,

    /**
     * Short {@link Short}.
     */
    SHORT,

    /**
     * String {@link String}.
     */
    STRING,

    /**
     * Time {@link java.sql.Time}.
     */
    TIME,

    /**
     * Timestamp {@link java.sql.Timestamp}.
     */
    TIMESTAMP,

    /**
     * Other sql types.
     */
    OTHER
}
