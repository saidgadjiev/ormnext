package ru.saidgadjiev.ormnext.core.field.persister;

import ru.saidgadjiev.ormnext.core.field.DataType;
import ru.saidgadjiev.ormnext.core.connection_source.DatabaseResults;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Type that persists a boolean type.
 */
public class BooleanDataPersister extends BaseDataPersister {

    /**
     * Create a new instance.
     */
    public BooleanDataPersister() {
        super(new Class[] {Boolean.class, boolean.class});
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
    public void setObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setBoolean(index, (Boolean) value);
    }

}
