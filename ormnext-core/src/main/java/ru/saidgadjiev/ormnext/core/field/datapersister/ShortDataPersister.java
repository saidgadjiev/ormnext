package ru.saidgadjiev.ormnext.core.field.datapersister;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.connection.PreparableObject;
import ru.saidgadjiev.ormnext.core.field.SqlType;

import java.sql.SQLException;
import java.sql.Types;

/**
 * Type that persists a short type.
 *
 * @author Said Gadjiev
 */
public class ShortDataPersister extends BaseDataPersister {

    /**
     * Create a new instance.
     */
    public ShortDataPersister() {
        super(new Class<?>[] {Short.class, short.class}, Types.SMALLINT, SqlType.SHORT);
    }

    @Override
    public Object readValue(DatabaseResults databaseResults, String columnLabel) throws SQLException {
        return databaseResults.getShort(columnLabel);
    }

    @Override
    protected void setNonNullObject(PreparableObject preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setShort(index, (Short) value);
    }
}
