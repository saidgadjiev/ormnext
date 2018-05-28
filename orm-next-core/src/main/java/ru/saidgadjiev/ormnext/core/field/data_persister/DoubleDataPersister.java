package ru.saidgadjiev.ormnext.core.field.data_persister;

import ru.saidgadjiev.ormnext.core.connection_source.DatabaseResults;
import ru.saidgadjiev.ormnext.core.field.DataType;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Type that persists a double type.
 *
 * @author said gadjiev
 */
public class DoubleDataPersister extends BaseDataPersister {

    /**
     * Create a new instance.
     */
    public DoubleDataPersister() {
        super(new Class[] {Double.class, double.class});
    }

    @Override
    public int getDataType() {
        return DataType.DOUBLE;
    }

    @Override
    public Object readValue(DatabaseResults databaseResults, String columnLabel) throws SQLException {
        return databaseResults.getDouble(columnLabel);
    }

    @Override
    public void setObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setDouble(index, (Double) value);
    }
}
