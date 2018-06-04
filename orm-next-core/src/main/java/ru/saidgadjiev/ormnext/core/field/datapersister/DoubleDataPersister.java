package ru.saidgadjiev.ormnext.core.field.datapersister;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.connection.OrmNextPreparedStatement;
import ru.saidgadjiev.ormnext.core.field.DataType;

import java.sql.SQLException;
import java.sql.Types;

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
        super(new Class[] {Double.class, double.class}, Types.DOUBLE);
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
    public void setNonNullObject(OrmNextPreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setDouble(index, (Double) value);
    }
}
