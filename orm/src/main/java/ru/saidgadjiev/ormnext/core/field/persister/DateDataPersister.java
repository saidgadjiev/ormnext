package ru.saidgadjiev.ormnext.core.field.persister;

import ru.saidgadjiev.ormnext.core.field.DataType;
import ru.saidgadjiev.ormnext.core.support.DatabaseResults;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class DateDataPersister extends BaseDataPersister<Byte> {

    public DateDataPersister() {
        super(new Class<?>[] {Date.class, java.sql.Date.class});
    }

    @Override
    public int getDataType() {
        return DataType.DATE;
    }

    @Override
    public Object readValue(DatabaseResults databaseResults, int column) throws SQLException {
        return databaseResults.getDate(column);
    }

    @Override
    public Object readValue(DatabaseResults databaseResults, String columnLabel) throws SQLException {
        return databaseResults.getDate(columnLabel);
    }

    @Override
    public void setObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setDate(index, (java.sql.Date) value);
    }
}
