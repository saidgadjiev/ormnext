package ru.saidgadjiev.ormnext.core.field.persister;

import ru.saidgadjiev.ormnext.core.field.DataType;
import ru.saidgadjiev.ormnext.core.support.DatabaseResults;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by said on 03.02.2018.
 */
public class FloatDataPersister extends BaseDataPersister {

    public FloatDataPersister() {
        super(new Class[] {Float.class, float.class});
    }

    @Override
    public int getDataType() {
        return DataType.FLOAT;
    }

    @Override
    public Object readValue(DatabaseResults databaseResults, int column) throws SQLException {
        return databaseResults.getFloat(column);
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
