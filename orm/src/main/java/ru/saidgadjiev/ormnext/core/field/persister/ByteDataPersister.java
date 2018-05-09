package ru.saidgadjiev.ormnext.core.field.persister;

import ru.saidgadjiev.ormnext.core.field.DataType;
import ru.saidgadjiev.ormnext.core.support.DatabaseResultSet;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ByteDataPersister extends BaseDataPersister<Byte> {

    public ByteDataPersister() {
        super(new Class<?>[] {Byte.class, byte.class});
    }

    @Override
    public int getDataType() {
        return DataType.BYTE;
    }

    @Override
    public Object readValue(DatabaseResultSet databaseResultSet, int column) throws SQLException {
        return databaseResultSet.getByte(column);
    }

    @Override
    public Object readValue(DatabaseResultSet databaseResultSet, String columnLabel) throws SQLException {
        return databaseResultSet.getByte(columnLabel);
    }

    @Override
    public void setObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setByte(index, (Byte) value);
    }
}
