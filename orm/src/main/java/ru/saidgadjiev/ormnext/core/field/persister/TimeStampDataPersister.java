package ru.saidgadjiev.ormnext.core.field.persister;

import ru.saidgadjiev.ormnext.core.field.DataType;
import ru.saidgadjiev.ormnext.core.support.DatabaseResults;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class TimeStampDataPersister extends BaseDataPersister<Byte> {

    public TimeStampDataPersister() {
        super(new Class<?>[] {Date.class, java.sql.Date.class, Timestamp.class});
    }

    @Override
    public int getDataType() {
        return DataType.TIMESTAMP;
    }

    @Override
    public Object readValue(DatabaseResults databaseResults, int column) throws SQLException {
        return databaseResults.getTimestamp(column);
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
