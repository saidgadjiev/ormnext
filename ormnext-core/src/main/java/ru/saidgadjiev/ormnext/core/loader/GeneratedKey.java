package ru.saidgadjiev.ormnext.core.loader;

import java.sql.SQLException;

/**
 * Interface for generated key holder.
 *
 * @author said gadjiev
 */
public interface GeneratedKey {

    /**
     * Provide generated key.
     * @param key target key
     * @throws SQLException any SQL exceptions
     */
    void set(Object key) throws SQLException;

    /**
     * Return generated key.
     * @return generated key.
     */
    Object get();
}
