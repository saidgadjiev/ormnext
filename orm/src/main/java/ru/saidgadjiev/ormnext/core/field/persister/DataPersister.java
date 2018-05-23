package ru.saidgadjiev.ormnext.core.field.persister;

import ru.saidgadjiev.ormnext.core.support.DatabaseResults;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface DataPersister<T> {

    Object parseDefaultTo(String value) throws Exception;

    boolean isValidForGenerated();

    Class<?>[] getAssociatedClasses();

    int getDataType();

    Object readValue(DatabaseResults databaseResults, int column) throws SQLException;

    Object readValue(DatabaseResults databaseResults, String columnLabel) throws SQLException;

    void setObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException;
}
