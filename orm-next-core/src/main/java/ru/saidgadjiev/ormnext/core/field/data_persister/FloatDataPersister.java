package ru.saidgadjiev.ormnext.core.field.data_persister;

import ru.saidgadjiev.ormnext.core.connection_source.DatabaseResults;
import ru.saidgadjiev.ormnext.core.field.DataType;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Type that persists a float type.
 */
public class FloatDataPersister extends BaseDataPersister {

    /**
     * Create a new instance.
     */
    public FloatDataPersister() {
        super(new Class[] {Float.class, float.class});
    }

    @Override
    public int getDataType() {
        return DataType.FLOAT;
    }

    @Override
    public Object readValue(DatabaseResults databaseResults, String columnLabel) throws SQLException {
        return databaseResults.getFloat(columnLabel);
    }

    @Override
    public void setObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setFloat(index, (Float) value);
    }
}
