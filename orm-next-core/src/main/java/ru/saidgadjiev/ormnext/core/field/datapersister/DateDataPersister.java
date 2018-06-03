package ru.saidgadjiev.ormnext.core.field.datapersister;

import ru.saidgadjiev.ormnext.core.connectionsource.DatabaseResults;
import ru.saidgadjiev.ormnext.core.field.DataType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
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
        super(new Class<?>[] {Date.class, java.sql.Date.class}, Types.DATE);
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
    protected void setNonNullObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setDate(index, (java.sql.Date) value);
    }
}
