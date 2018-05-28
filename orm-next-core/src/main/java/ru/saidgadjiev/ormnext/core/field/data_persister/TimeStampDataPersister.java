package ru.saidgadjiev.ormnext.core.field.data_persister;

import ru.saidgadjiev.ormnext.core.connection_source.DatabaseResults;
import ru.saidgadjiev.ormnext.core.field.DataType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Type that persists a timestamp type.
 */
public class TimeStampDataPersister extends BaseDataPersister {

    /**
     * Create a new instance.
     */
    public TimeStampDataPersister() {
        super(new Class<?>[] {Date.class, java.sql.Date.class, Timestamp.class});
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
    public void setObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setTimestamp(index, (Timestamp) value);
    }
}
