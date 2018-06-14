package ru.saidgadjiev.ormnext.core.field.datapersister;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.connection.PreparableObject;
import ru.saidgadjiev.ormnext.core.field.SqlType;

import java.sql.SQLException;
import java.sql.Types;

/**
 * Type that persists a byte type.
 *
 * @author Said Gadjiev
 */
public class ByteDataPersister extends BaseDataPersister {

    /**
     * Create a new instance.
     */
    public ByteDataPersister() {
        super(new Class<?>[] {Byte.class, byte.class}, Types.TINYINT, SqlType.BYTE);
    }

    @Override
    public Object readValue(DatabaseResults databaseResults, String columnLabel) throws SQLException {
        return databaseResults.getByte(columnLabel);
    }

    @Override
    protected void setNonNullObject(PreparableObject preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setByte(index, (Byte) value);
    }
}
