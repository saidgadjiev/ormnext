package ru.saidgadjiev.ormnext.core.field.datapersister;

import ru.saidgadjiev.ormnext.core.connectionsource.DatabaseResults;
import ru.saidgadjiev.ormnext.core.field.DataType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Type that persists a integer type.
 *
 * @author said gadjiev
 */
public class IntegerDataPersister extends BaseDataPersister {

    /**
     * Create a new instance.
     */
    public IntegerDataPersister() {
        super(new Class<?>[]{Integer.class, int.class}, Types.INTEGER);
    }

    @Override
    public int getDataType() {
        return DataType.INTEGER;
    }

    @Override
    public Object readValue(DatabaseResults databaseResults, String columnLabel) throws SQLException {
        return databaseResults.getInt(columnLabel);
    }

    @Override
    protected void setNonNullObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setInt(index, (Integer) value);
    }
}
