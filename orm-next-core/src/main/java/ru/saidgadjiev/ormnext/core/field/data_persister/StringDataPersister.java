package ru.saidgadjiev.ormnext.core.field.data_persister;

import ru.saidgadjiev.ormnext.core.field.DataType;
import ru.saidgadjiev.ormnext.core.connection_source.DatabaseResults;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Type that persists a string type.
 */
public class StringDataPersister extends BaseDataPersister {

    /**
     * Create a new instance.
     */
    public StringDataPersister() {
        super(new Class<?>[] {String.class});
    }

    @Override
    public int getDataType() {
        return DataType.STRING;
    }

    @Override
    public Object readValue(DatabaseResults databaseResults, String columnLabel) throws SQLException {
        return databaseResults.getString(columnLabel);
    }

    @Override
    public void setObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setString(index, (String) value);
    }
}
