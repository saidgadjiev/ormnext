package ru.saidgadjiev.ormnext.core.field.field_type;

import ru.saidgadjiev.ormnext.core.field.data_persister.ColumnConverter;

import java.util.List;
import java.util.Optional;

/**
 * Base class for database column types.
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
    public boolean isDbColumnType() {
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
}
