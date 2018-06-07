package ru.saidgadjiev.ormnext.core.field.datapersister;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.connection.PreparableObject;
import ru.saidgadjiev.ormnext.core.field.DataType;

import java.sql.SQLException;
import java.sql.Types;

/**
 * Type that persists a boolean type.
 *
 * @author Said Gadjiev
 */
public class BooleanDataPersister extends BaseDataPersister {

    /**
     * Create a new instance.
     */
    public BooleanDataPersister() {
        super(new Class[] {Boolean.class, boolean.class}, Types.BOOLEAN);
    }

    @Override
    public int getDataType() {
        return DataType.BOOLEAN;
    }

    @Override
    public Object readValue(DatabaseResults databaseResults, String columnLabel) throws SQLException {
        return databaseResults.getBoolean(columnLabel);
    }

    @Override
    protected void setNonNullObject(PreparableObject preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setBoolean(index, (Boolean) value);
    }
}
