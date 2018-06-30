package ru.saidgadjiev.ormnext.core.connection;

import java.sql.SQLException;
import java.util.Set;

/**
 * Database results metadata.
 *
 * @author Said Gadjiev
 */
public interface DatabaseResultsMetadata {

    /**
     * Result column names.
     *
     * @return column names
     * @throws SQLException throws SQL exceptions
     */
    Set<String> getResultColumnNames() throws SQLException;

    /**
     * Return original metadata object.
     *
     * @param <T> Metadata type
     * @return metadata object
     */
    <T> T getResultSetMetaDataObject();
}
