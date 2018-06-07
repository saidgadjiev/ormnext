package ru.saidgadjiev.ormnext.core.field.datapersister;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.connection.PreparableObject;
import ru.saidgadjiev.ormnext.core.field.DataType;

import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.util.Date;

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
        super(new Class<?>[] {Time.class, Date.class, java.sql.Date.class}, Types.TIME);
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
    protected void setNonNullObject(PreparableObject preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setTime(index, (Time) value);
    }
}
