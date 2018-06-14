package ru.saidgadjiev.ormnext.core.field.fieldtype;

import ru.saidgadjiev.ormnext.core.field.SqlType;
import ru.saidgadjiev.ormnext.core.field.datapersister.ColumnConverter;

import java.util.List;
import java.util.Optional;

/**
 * Base class for database column types.
 *
 * @author Said Gadjiev
 */
public abstract class BaseDatabaseColumnType implements DatabaseColumnType {

    @Override
    public String defaultDefinition() {
        return null;
    }

    @Override
    public boolean id() {
        return false;
    }

    @Override
    public boolean notNull() {
        return false;
    }

    @Override
    public boolean generated() {
        return false;
    }

    @Override
    public String columnName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SqlType ormNextSqlType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int sqlType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int length() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean databaseColumnType() {
        return false;
    }

    @Override
    public boolean foreignColumnType() {
        return false;
    }

    @Override
    public boolean foreignCollectionColumnType() {
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

    @Override
    public boolean defineInCreateTable() {
        return true;
    }

    @Override
    public Class<?> getType() {
        return getField().getType();
    }

    @Override
    public SqlType foreignOrmNextSqlType() {
        return dataPersister().getForeignOrmNextSqlType();
    }
}
