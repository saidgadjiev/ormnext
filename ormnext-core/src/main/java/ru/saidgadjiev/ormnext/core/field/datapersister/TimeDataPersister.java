package ru.saidgadjiev.ormnext.core.field.datapersister;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.connection.PreparableObject;
import ru.saidgadjiev.ormnext.core.field.SqlType;

import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;

/**
 * Type that persists a time type.
 *
 * @author Said Gadjiev
 */
public class TimeDataPersister extends BaseDataPersister {

    /**
     * Create a new instance.
     */
    public TimeDataPersister() {
        super(new Class<?>[] {Time.class}, Types.TIME, SqlType.TIME);
    }

    @Override
    public Object readValue(DatabaseResults databaseResults, String columnLabel) throws SQLException {
        return databaseResults.getTime(columnLabel);
    }

    @Override
    protected void setNonNullObject(PreparableObject preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setTime(index, (Time) value);
    }
}
