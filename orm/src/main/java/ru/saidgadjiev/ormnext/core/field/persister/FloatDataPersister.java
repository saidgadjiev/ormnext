package ru.saidgadjiev.ormnext.core.field.persister;

import ru.saidgadjiev.ormnext.core.field.DataType;
import ru.saidgadjiev.ormnext.core.support.DatabaseResultSet;

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
    public Object readValue(DatabaseResultSet databaseResultSet, int column) throws SQLException {
        return databaseResultSet.getFloat(column);
    }

    @Override
    public Object readValue(DatabaseResultSet databaseResultSet, String columnLabel) throws SQLException {
        return databaseResultSet.getFloat(columnLabel);
    }

    @Override
    public void setObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setFloat(index, (Float) value);
    }
}
