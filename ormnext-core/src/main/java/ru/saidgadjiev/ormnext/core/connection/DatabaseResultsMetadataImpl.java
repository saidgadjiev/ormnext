package ru.saidgadjiev.ormnext.core.connection;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Database results metadata implementation fro {@link ResultSetMetaData}.
 *
 * @author Said Gadjiev
 */
public class DatabaseResultsMetadataImpl implements DatabaseResultsMetadata {

    /**
     * MetaData object.
     */
    private final ResultSetMetaData resultSetMetaData;

    /**
     * Create a new instance.
     *
     * @param resultSetMetaData target metadata
     */
    public DatabaseResultsMetadataImpl(ResultSetMetaData resultSetMetaData) {
        this.resultSetMetaData = resultSetMetaData;

    }

    @Override
    public Set<String> getResultColumnNames() throws SQLException {
        Set<String> columns = new HashSet<>();

        for (int i = 1; i < resultSetMetaData.getColumnCount() + 1; ++i) {
            columns.add(resultSetMetaData.getColumnName(i));
        }

        return columns;
    }

    @Override
    public<T> T getResultSetMetaDataObject() {
        return (T) resultSetMetaData;
    }
}
