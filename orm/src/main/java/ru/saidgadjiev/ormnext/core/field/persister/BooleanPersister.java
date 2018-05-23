package ru.saidgadjiev.ormnext.core.field.persister;

import ru.saidgadjiev.ormnext.core.field.DataType;
import ru.saidgadjiev.ormnext.core.support.DatabaseResults;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BooleanPersister extends BaseDataPersister {

    public BooleanPersister() {
        super(new Class[] {Boolean.class, boolean.class });
    }

    @Override
    public int getDataType() {
        return DataType.BOOLEAN;
    }

    @Override
    public Object readValue(DatabaseResults databaseResults, int column) throws SQLException {
        return databaseResults.getBoolean(column);
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
