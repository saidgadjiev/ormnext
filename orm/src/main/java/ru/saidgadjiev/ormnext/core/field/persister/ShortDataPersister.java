package ru.saidgadjiev.ormnext.core.field.persister;

import ru.saidgadjiev.ormnext.core.field.DataType;
import ru.saidgadjiev.ormnext.core.support.DatabaseResultSet;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ShortDataPersister extends BaseDataPersister<Byte> {

    public ShortDataPersister() {
        super(new Class<?>[] {Short.class, short.class});
    }

    @Override
    public int getDataType() {
        return DataType.SHORT;
    }

    @Override
    public Object readValue(DatabaseResultSet databaseResultSet, int column) throws SQLException {
        return databaseResultSet.getShort(column);
    }

    @Override
    public Object readValue(DatabaseResultSet databaseResultSet, String columnLabel) throws SQLException {
        return databaseResultSet.getShort(columnLabel);
    }

    @Override
    public void setObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setShort(index, (Short) value);
    }
}
