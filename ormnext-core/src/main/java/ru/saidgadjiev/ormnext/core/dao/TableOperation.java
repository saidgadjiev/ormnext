package ru.saidgadjiev.ormnext.core.dao;

/**
 * Table operations.
 *
 * @author Said Gadjiev
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
