package ru.saidgadjiev.ormnext.core.field.datapersister;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.connection.PreparableObject;
import ru.saidgadjiev.ormnext.core.field.DataType;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

/**
 * Type that persists a date type.
 *
 * @author Said Gadjiev
 */
public class DateDataPersister extends BaseDataPersister {

    /**
     * Create a new instance.
     */
    public DateDataPersister() {
        super(new Class<?>[] {Date.class, java.sql.Date.class}, Types.DATE);
    }

    @Override
    public int getSqlType() {
        return DataType.DATE;
    }

    @Override
    public Object readValue(DatabaseResults databaseResults, String columnLabel) throws SQLException {
        return databaseResults.getDate(columnLabel);
    }

    @Override
    protected void setNonNullObject(PreparableObject preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setDate(index, (java.sql.Date) value);
    }
}
