package ru.saidgadjiev.ormnext.core.field.datapersister;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.connection.OrmNextPreparedStatement;
import ru.saidgadjiev.ormnext.core.field.DataType;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

/**
 * Type that persists a timestamp type.
 *
 * @author said gadjiev
 */
public class TimeStampDataPersister extends BaseDataPersister {

    /**
     * Create a new instance.
     */
    public TimeStampDataPersister() {
        super(new Class<?>[] {Date.class, java.sql.Date.class, Timestamp.class}, Types.TIMESTAMP);
    }

    @Override
    public int getDataType() {
        return DataType.TIMESTAMP;
    }

    @Override
    public Object readValue(DatabaseResults databaseResults, String columnLabel) throws SQLException {
        return databaseResults.getTimestamp(columnLabel);
    }

    @Override
    protected void setNonNullObject(OrmNextPreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setTimestamp(index, (Timestamp) value);
    }
}
