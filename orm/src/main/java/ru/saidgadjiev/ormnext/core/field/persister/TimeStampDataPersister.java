package ru.saidgadjiev.ormnext.core.field.persister;

import ru.saidgadjiev.ormnext.core.field.DataType;
import ru.saidgadjiev.ormnext.core.support.DatabaseResultSet;

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
    public Object readValue(DatabaseResultSet databaseResultSet, int column) throws SQLException {
        return databaseResultSet.getTimestamp(column);
    }

    @Override
    public Object readValue(DatabaseResultSet databaseResultSet, String columnLabel) throws SQLException {
        return databaseResultSet.getTimestamp(columnLabel);
    }

    @Override
    public void setObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setTimestamp(index, (java.sql.Timestamp) value);
    }
}
