package ru.saidgadjiev.ormnext.core.field.fieldtype;

import ru.saidgadjiev.ormnext.core.field.datapersister.ColumnConverter;

import java.util.List;
import java.util.Optional;

/**
 * Base class for database column types.
 *
 * @author said gadjiev
 */
public abstract class BaseDatabaseColumnType implements IDatabaseColumnType {

    @Override
    public String getDefaultDefinition() {
        return null;
    }

    @Override
    public boolean isId() {
        return false;
    }

    @Override
    public boolean isNotNull() {
        return false;
    }

    @Override
    public boolean isGenerated() {
        return false;
    }

    @Override
    public String getColumnName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDataType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public boolean isDatabaseColumnType() {
        return false;
    }

    @Override
    public boolean isForeignColumnType() {
        return false;
    }

    @Override
    public boolean isForeignCollectionColumnType() {
        return false;
    }

    @Override
    public Optional<List<ColumnConverter<?, Object>>> getColumnConverters() {
        return Optional.empty();
    }

    @Override
    public boolean defaultIfNull() {
        return false;
    }

    @Override
    public boolean unique() {
        return false;
    }
}
