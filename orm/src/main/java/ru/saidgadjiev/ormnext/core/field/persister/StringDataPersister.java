package ru.saidgadjiev.ormnext.core.field.persister;

import ru.saidgadjiev.ormnext.core.field.DataType;
import ru.saidgadjiev.ormnext.core.support.DatabaseResults;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StringDataPersister extends BaseDataPersister {

    public StringDataPersister() {
        super(new Class<?>[] { String.class });
    }

    @Override
    public int getDataType() {
        return DataType.STRING;
    }

    @Override
    public Object readValue(DatabaseResults databaseResults, int column) throws SQLException {
        return databaseResults.getString(column);
    }

    @Override
    public Object readValue(DatabaseResults databaseResults, String columnLabel) throws SQLException {
        return databaseResults.getString(columnLabel);
    }

    @Override
    public void setObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setString(index, (String) value);
    }
}
