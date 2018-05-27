package ru.saidgadjiev.ormnext.core.field.persister;

import ru.saidgadjiev.ormnext.core.field.DataType;
import ru.saidgadjiev.ormnext.core.connection_source.DatabaseResults;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Type that persists a short type.
 */
public class ShortDataPersister extends BaseDataPersister {

    /**
     * Create a new instance.
     */
    public ShortDataPersister() {
        super(new Class<?>[] {Short.class, short.class});
    }

    @Override
    public int getDataType() {
        return DataType.SHORT;
    }

    @Override
    public Object readValue(DatabaseResults databaseResults, String columnLabel) throws SQLException {
        return databaseResults.getShort(columnLabel);
    }

    @Override
    public void setObject(PreparedStatement preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setShort(index, (Short) value);
    }
}
