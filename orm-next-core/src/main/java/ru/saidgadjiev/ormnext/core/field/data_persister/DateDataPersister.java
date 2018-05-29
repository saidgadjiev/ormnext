package ru.saidgadjiev.ormnext.core.field.data_persister;

import ru.saidgadjiev.ormnext.core.connection_source.DatabaseResults;
import ru.saidgadjiev.ormnext.core.field.DataType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

/**
 * Type that persists a date type.
 *
 * @author said gadjiev
 */
public class DateDataPersister extends BaseDataPersister {

    /**
     * Create a new instance.
     */
    public DateDataPersister() {
        super(new Class<?>[] {Date.class, java.sql.Date.class});
    }

    @Override
    public int getDataType() {
        return DataType.DATE;
    }

    @Override
    public Object readValue(DatabaseResults databaseResults, String columnLabel) throws SQLException {
        return databaseResults.getDate(columnLabel);
    }

    @Override
    public void setObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setDate(index, (java.sql.Date) value);
    }
}
