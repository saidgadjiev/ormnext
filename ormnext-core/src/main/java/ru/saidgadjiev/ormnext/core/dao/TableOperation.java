package ru.saidgadjiev.ormnext.core.dao;

/**
 * Table operations.
 *
 * @author said gadjiev
 */
public enum TableOperation {

    /**
     * Create tables.
     */
    CREATE,

    /**
     * Clear tables.
     */
    CLEAR,

    /**
     * Drop and create tables.
     */
    DROP_CREATE,

    /**
     * No action.
     */
    NO_ACTION
}
