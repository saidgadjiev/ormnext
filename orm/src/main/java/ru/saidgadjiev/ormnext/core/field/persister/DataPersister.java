package ru.saidgadjiev.ormnext.core.field.persister;

import ru.saidgadjiev.ormnext.core.support.DatabaseResultSet;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface DataPersister<T> {

    Object parseDefaultTo(String value) throws Exception;

    boolean isValidForGenerated();

    Class<?>[] getAssociatedClasses();

    int getDataType();

    Object readValue(DatabaseResultSet databaseResultSet, int column) throws SQLException;

    Object readValue(DatabaseResultSet databaseResultSet, String columnLabel) throws SQLException;

    void setObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException;
}
