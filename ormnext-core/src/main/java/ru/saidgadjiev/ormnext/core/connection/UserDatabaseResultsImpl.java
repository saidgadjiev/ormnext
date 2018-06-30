package ru.saidgadjiev.ormnext.core.connection;

import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;

import java.sql.*;

/**
 * Database result set decorator for access by property name.
 *
 * @author Said Gadjiev
 */
public class UserDatabaseResultsImpl implements DatabaseResults {

    /**
     * Entity aliases.
     */
    private EntityAliases entityAliases;

    /**
     * Database results.
     */
    private DatabaseResults databaseResults;

    /**
     * Create a new instance.
     *
     * @param entityAliases target entity aliases
     * @param databaseResults target database results
     */
    public UserDatabaseResultsImpl(EntityAliases entityAliases,
                                   DatabaseResults databaseResults) {
        this.entityAliases = entityAliases;
        this.databaseResults = databaseResults;
    }

    @Override
    public boolean next() throws SQLException {
        return databaseResults.next();
    }

    @Override
    public String getString(int columnId) throws SQLException {
        return databaseResults.getString(columnId);
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        if (canResolveAlias(columnLabel)) {
            String alias = entityAliases.getAliasByPropertyName(columnLabel);

            return databaseResults.getString(alias);
        }

        return databaseResults.getString(columnLabel);
    }

    @Override
    public int getInt(int columnId) throws SQLException {
        return databaseResults.getInt(columnId);
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        if (canResolveAlias(columnLabel)) {
            String alias = entityAliases.getAliasByPropertyName(columnLabel);

            return databaseResults.getInt(alias);
        }

        return databaseResults.getInt(columnLabel);
    }

    @Override
    public boolean getBoolean(int columnId) throws SQLException {
        return databaseResults.getBoolean(columnId);
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        if (canResolveAlias(columnLabel)) {
            String alias = entityAliases.getAliasByPropertyName(columnLabel);

            return databaseResults.getBoolean(alias);
        }

        return databaseResults.getBoolean(columnLabel);
    }

    @Override
    public double getDouble(int columnId) throws SQLException {
        return databaseResults.getDouble(columnId);
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        if (canResolveAlias(columnLabel)) {
            String alias = entityAliases.getAliasByPropertyName(columnLabel);

            return databaseResults.getDouble(alias);
        }

        return databaseResults.getDouble(columnLabel);
    }

    @Override
    public float getFloat(int columnId) throws SQLException {
        return databaseResults.getFloat(columnId);
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        if (canResolveAlias(columnLabel)) {
            String alias = entityAliases.getAliasByPropertyName(columnLabel);

            return databaseResults.getFloat(alias);
        }

        return databaseResults.getFloat(columnLabel);
    }

    @Override
    public Long getLong(int columnId) throws SQLException {
        return databaseResults.getLong(columnId);
    }

    @Override
    public Long getLong(String columnLabel) throws SQLException {
        if (canResolveAlias(columnLabel)) {
            String alias = entityAliases.getAliasByPropertyName(columnLabel);

            return databaseResults.getLong(alias);
        }

        return databaseResults.getLong(columnLabel);
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        if (canResolveAlias(columnLabel)) {
            String alias = entityAliases.getAliasByPropertyName(columnLabel);

            return databaseResults.getByte(alias);
        }

        return databaseResults.getByte(columnLabel);
    }

    @Override
    public byte getByte(int columnId) throws SQLException {
        return databaseResults.getByte(columnId);
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        if (canResolveAlias(columnLabel)) {
            String alias = entityAliases.getAliasByPropertyName(columnLabel);

            return databaseResults.getShort(alias);
        }

        return databaseResults.getShort(columnLabel);
    }

    @Override
    public short getShort(int columnId) throws SQLException {
        return databaseResults.getShort(columnId);
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        if (canResolveAlias(columnLabel)) {
            String alias = entityAliases.getAliasByPropertyName(columnLabel);

            return databaseResults.getDate(alias);
        }

        return databaseResults.getDate(columnLabel);
    }

    @Override
    public Date getDate(int column) throws SQLException {
        return databaseResults.getDate(column);
    }

    @Override
    public Time getTime(int column) throws SQLException {
        return databaseResults.getTime(column);
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        if (canResolveAlias(columnLabel)) {
            String alias = entityAliases.getAliasByPropertyName(columnLabel);

            return databaseResults.getTime(alias);
        }

        return databaseResults.getTime(columnLabel);
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        if (canResolveAlias(columnLabel)) {
            String alias = entityAliases.getAliasByPropertyName(columnLabel);

            return databaseResults.getTimestamp(alias);
        }

        return databaseResults.getTimestamp(columnLabel);
    }

    @Override
    public Timestamp getTimestamp(int column) throws SQLException {
        return databaseResults.getTimestamp(column);
    }

    @Override
    public boolean wasNull() throws SQLException {
        return databaseResults.wasNull();
    }

    @Override
    public <T> T getResultsObject() {
        return (T) databaseResults.getResultsObject();
    }

    @Override
    public DatabaseResultsMetadata getMetaData() throws SQLException {
        return databaseResults.getMetaData();
    }

    @Override
    public void close() throws SQLException {
        databaseResults.close();
    }

    /**
     * Return true if property can be resolved.
     *
     * @param propertyName target property name
     * @return true if property can be resolved
     */
    private boolean canResolveAlias(String propertyName) {
        if (entityAliases == null) {
            return false;
        }
        if (entityAliases.getAliasByPropertyName(propertyName) == null) {
            return false;
        }

        return true;
    }
}
