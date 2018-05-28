package ru.saidgadjiev.ormnext.core.field.data_persister;

import ru.saidgadjiev.ormnext.core.connection_source.DatabaseResults;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Column data persister.
 */
public interface DataPersister {

    /**
     * Associated java classes with this type.
     * @return associated classes
     * @see ru.saidgadjiev.ormnext.core.field.DataPersisterManager
     * @see ru.saidgadjiev.ormnext.core.field.DataType
     */
    List<Class<?>> getAssociatedClasses();

    /**
     * Associated data type.
     * @return data type
     */
    int getDataType();

    /**
     * Read value from database results by column name.
     * @param databaseResults target results
     * @param columnLabel target column name
     * @return read value
     * @throws SQLException any SQL exceptions
     */
    Object readValue(DatabaseResults databaseResults, String columnLabel) throws SQLException;

    /**
     * Set value to prepared statement {@link PreparedStatement}.
     * @param preparedStatement target prepared statement
     * @param index target index
     * @param value target value
     * @throws SQLException any SQL exceptions
     */
    void setObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException;
}
