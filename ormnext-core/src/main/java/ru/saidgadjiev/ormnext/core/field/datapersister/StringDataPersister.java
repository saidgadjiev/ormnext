package ru.saidgadjiev.ormnext.core.field.datapersister;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.connection.PreparableObject;
import ru.saidgadjiev.ormnext.core.field.SqlType;

import java.sql.SQLException;
import java.sql.Types;

/**
 * Type that persists a string type.
 *
 * @author Said Gadjiev
 */
public class StringDataPersister extends BaseDataPersister {

    /**
     * Create a new instance.
     */
    public StringDataPersister() {
        super(new Class<?>[] {String.class}, Types.VARCHAR, SqlType.STRING);
    }

    @Override
    public Object readValue(DatabaseResults databaseResults, String columnLabel) throws SQLException {
        return databaseResults.getString(columnLabel);
    }

    @Override
    protected void setNonNullObject(PreparableObject preparedStatement, int index, Object value) throws SQLException {
        preparedStatement.setString(index, (String) value);
    }
}
