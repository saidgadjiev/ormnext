package ru.saidgadjiev.ormnext.core.field.persister;

import ru.saidgadjiev.ormnext.core.field.DataType;
import ru.saidgadjiev.ormnext.core.support.DatabaseResultSet;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class IntegerDataPersister extends BaseDataPersister {

    public IntegerDataPersister() {
        super(new Class<?>[] {Integer.class, int.class, Long.class, long.class});
    }

    @Override
    public int getDataType() {
        return DataType.INTEGER;
    }

    @Override
    public Object readValue(DatabaseResultSet databaseResultSet, int column) throws SQLException {
        return databaseResultSet.getInt(column);
    }

    @Override
    public Object readValue(DatabaseResultSet databaseResultSet, String columnLabel) throws SQLException {
        return databaseResultSet.getInt(columnLabel);
    }

    @Override
    public void setObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setInt(index, (Integer) value);
    }

    @Override
    public boolean isValidForGenerated() {
        return true;
    }
}
