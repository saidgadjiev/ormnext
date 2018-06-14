package ru.saidgadjiev.ormnext.core.field.datapersister;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.connection.PreparableObject;
import ru.saidgadjiev.ormnext.core.field.SqlType;

import java.sql.SQLException;
import java.sql.Types;

/**
 * Type that persists a integer type.
 *
 * @author Said Gadjiev
 */
public class IntegerDataPersister extends BaseDataPersister {

    /**
     * Create a new instance.
     */
    public IntegerDataPersister() {
        super(new Class<?>[]{Integer.class, int.class}, Types.INTEGER, SqlType.INTEGER);
    }

    @Override
    public Object readValue(DatabaseResults databaseResults, String columnLabel) throws SQLException {
        return databaseResults.getInt(columnLabel);
    }

    @Override
    protected void setNonNullObject(PreparableObject preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setInt(index, (Integer) value);
    }

    @Override
    public Object convertToPrimaryKey(Object value) {
        return ((Number) value).intValue();
    }
}
