package ru.saidgadjiev.ormnext.core.field.datapersister;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.connection.PreparableObject;
import ru.saidgadjiev.ormnext.core.field.SqlType;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * Type that persists a timestamp type.
 *
 * @author Said Gadjiev
 */
public class TimeStampDataPersister extends BaseDataPersister {

    /**
     * Create a new instance.
     */
    public TimeStampDataPersister() {
        super(new Class<?>[] {Timestamp.class}, Types.TIMESTAMP, SqlType.TIMESTAMP);
    }

    @Override
    public Object readValue(DatabaseResults databaseResults, String columnLabel) throws SQLException {
        return databaseResults.getTimestamp(columnLabel);
    }

    @Override
    protected void setNonNullObject(PreparableObject preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setTimestamp(index, (Timestamp) value);
    }
}
