package ru.saidgadjiev.ormnext.core.field.data_persister;

import ru.saidgadjiev.ormnext.core.connection_source.DatabaseResults;
import ru.saidgadjiev.ormnext.core.field.DataType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;

/**
 * Type that persists a time type.
 */
public class TimeDataPersister extends BaseDataPersister {

    /**
     * Create a new instance.
     */
    public TimeDataPersister() {
        super(new Class<?>[] {Time.class, Date.class, java.sql.Date.class});
    }

    @Override
    public int getDataType() {
        return DataType.TIME;
    }

    @Override
    public Object readValue(DatabaseResults databaseResults, String columnLabel) throws SQLException {
        return databaseResults.getTime(columnLabel);
    }

    @Override
    public void setObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setTime(index, (Time) value);
    }
}
