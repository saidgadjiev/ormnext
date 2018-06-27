package ru.saidgadjiev.ormnext.core.connection;

import java.sql.SQLException;
import java.util.Set;

public interface DatabaseResultsMetadata {

    Set<String> getResultColumnNames() throws SQLException;

    <T> T getResultSetMetaDataObject();
}
