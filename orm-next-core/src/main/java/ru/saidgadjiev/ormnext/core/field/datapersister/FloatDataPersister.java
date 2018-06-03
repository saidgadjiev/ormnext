package ru.saidgadjiev.ormnext.core.field.datapersister;

import ru.saidgadjiev.ormnext.core.connectionsource.DatabaseResults;
import ru.saidgadjiev.ormnext.core.field.DataType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Type that persists a float type.
 *
 * @author said gadjiev
 */
public class FloatDataPersister extends BaseDataPersister {

    /**
     * Create a new instance.
     */
    public FloatDataPersister() {
        super(new Class[] {Float.class, float.class}, Types.FLOAT);
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
    public void setNonNullObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setFloat(index, (Float) value);
    }
}
