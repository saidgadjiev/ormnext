package ru.saidgadjiev.ormnext.core.field.data_persister;

import ru.saidgadjiev.ormnext.core.connection_source.DatabaseResults;
import ru.saidgadjiev.ormnext.core.field.DataType;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Type that persists a integer type.
 */
public class IntegerDataPersister extends BaseDataPersister {

    /**
     * Create a new instance.
     */
    public IntegerDataPersister() {
        super(new Class<?>[] {Integer.class, int.class, Long.class, long.class});
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
    public void setObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setInt(index, (Integer) value);
    }

}
