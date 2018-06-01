package ru.saidgadjiev.ormnext.core.field.datapersister;

import ru.saidgadjiev.ormnext.core.connectionsource.DatabaseResults;
import ru.saidgadjiev.ormnext.core.field.DataType;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Type that persists a byte type.
 *
 * @author said gadjiev
 */
public class ByteDataPersister extends BaseDataPersister {

    /**
     * Create a new instance.
     */
    public ByteDataPersister() {
        super(new Class<?>[] {Byte.class, byte.class});
    }

    @Override
    public int getDataType() {
        return DataType.BYTE;
    }

    @Override
    public Object readValue(DatabaseResults databaseResults, String columnLabel) throws SQLException {
        return databaseResults.getByte(columnLabel);
    }

    @Override
    public void setObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setByte(index, (Byte) value);
    }
}
