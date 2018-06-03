package ru.saidgadjiev.ormnext.core.field.datapersister;

import ru.saidgadjiev.ormnext.core.connectionsource.DatabaseResults;
import ru.saidgadjiev.ormnext.core.field.DataType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Type that persists a string type.
 *
 * @author said gadjiev
 */
public class StringDataPersister extends BaseDataPersister {

    /**
     * Create a new instance.
     */
    public StringDataPersister() {
        super(new Class<?>[] {String.class}, Types.VARCHAR);
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
    protected void setNonNullObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setString(index, (String) value);
    }
}
