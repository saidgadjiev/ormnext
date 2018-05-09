package ru.saidgadjiev.ormnext.core.field.persister;

import ru.saidgadjiev.ormnext.core.field.DataType;
import ru.saidgadjiev.ormnext.core.support.DatabaseResultSet;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LongDataPersister extends BaseDataPersister {

    public LongDataPersister() {
        super(new Class<?>[] {Long.class, long.class});
    }

    @Override
    public int getDataType() {
        return DataType.LONG;
    }

    @Override
    public Object readValue(DatabaseResultSet databaseResultSet, int column) throws SQLException {
        return databaseResultSet.getLong(column);
    }

    @Override
    public Object readValue(DatabaseResultSet databaseResultSet, String columnLabel) throws SQLException {
        return databaseResultSet.getLong(columnLabel);
    }

    @Override
    public void setObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setLong(index, (Integer) value);
    }

    @Override
    public boolean isValidForGenerated() {
        return true;
    }
}
