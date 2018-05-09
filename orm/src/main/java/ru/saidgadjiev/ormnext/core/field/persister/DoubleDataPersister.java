package ru.saidgadjiev.ormnext.core.field.persister;

import ru.saidgadjiev.ormnext.core.field.DataType;
import ru.saidgadjiev.ormnext.core.support.DatabaseResultSet;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by said on 03.02.2018.
 */
public class DoubleDataPersister extends BaseDataPersister {

    public DoubleDataPersister() {
        super(new Class[] { Double.class, double.class});
    }

    @Override
    public int getDataType() {
        return DataType.DOUBLE;
    }

    @Override
    public Object readValue(DatabaseResultSet databaseResultSet, int column) throws SQLException {
        return databaseResultSet.getDouble(column);
    }

    @Override
    public Object readValue(DatabaseResultSet databaseResultSet, String columnLabel) throws SQLException {
        return databaseResultSet.getDouble(columnLabel);
    }

    @Override
    public void setObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setDouble(index, (Double) value);
    }
}
