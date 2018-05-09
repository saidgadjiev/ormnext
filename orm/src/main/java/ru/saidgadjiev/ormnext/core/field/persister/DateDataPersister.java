package ru.saidgadjiev.ormnext.core.field.persister;

import ru.saidgadjiev.ormnext.core.field.DataType;
import ru.saidgadjiev.ormnext.core.support.DatabaseResultSet;

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
    public Object readValue(DatabaseResultSet databaseResultSet, int column) throws SQLException {
        return databaseResultSet.getDate(column);
    }

    @Override
    public Object readValue(DatabaseResultSet databaseResultSet, String columnLabel) throws SQLException {
        return databaseResultSet.getDate(columnLabel);
    }

    @Override
    public void setObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setDate(index, (java.sql.Date) value);
    }
}
