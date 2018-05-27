package ru.saidgadjiev.ormnext.core.stament_executor;

import java.sql.SQLException;

/**
 * Interface for generated key holder.
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
