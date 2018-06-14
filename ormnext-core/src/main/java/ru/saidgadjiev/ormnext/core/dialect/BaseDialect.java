package ru.saidgadjiev.ormnext.core.dialect;

import ru.saidgadjiev.ormnext.core.exception.UnknownTypeException;
import ru.saidgadjiev.ormnext.core.field.SqlType;
import ru.saidgadjiev.ormnext.core.query.visitor.element.AttributeDefinition;

/**
 * Base class for all of the {@link Dialect} classes that provide the per-database type functionality to create
 * tables and build queries.
 *
 * @author Said Gadjiev
 */
public abstract class BaseDialect implements Dialect {

    @Override
    public String getTypeSql(AttributeDefinition def) {
        SqlType sqlType = def.getSqlType();

        switch (sqlType) {
            case BOOLEAN:
                return getBooleanTypeSql(def);
            case BYTE:
                return getByteTypeSql(def);
            case DATE:
                return getDateTypeSql(def);
            case DOUBLE:
                return getDoubleTypeSql(def);
            case FLOAT:
                return getFloatTypeSql(def);
            case INTEGER:
                return getIntegerTypeSql(def);
            case LONG:
                return getLongTypeSql(def);
            case SHORT:
                return getShortTypeSql(def);
            case STRING:
                return getStringTypeSql(def);
            case TIME:
                return getTimeTypeSql(def);
            case TIMESTAMP:
                return getTimestampTypeSql(def);
            case OTHER:
                return def.getDatabaseColumnType().dataPersister().getOtherTypeSql(this, def);
            default:
                throw new UnknownTypeException(def.getSqlType());
        }
    }

    /**
     * Get {@link SqlType#TIMESTAMP} type present.
     *
     * @param def target attr def
     * @return type sql present
     */
    @SuppressWarnings("PMD")
    private String getTimestampTypeSql(AttributeDefinition def) {
        return "TIMESTAMP";
    }

    /**
     * Get {@link SqlType#TIME} type present.
     *
     * @param def target attr def
     * @return type sql present
     */
    @SuppressWarnings("PMD")
    private String getTimeTypeSql(AttributeDefinition def) {
        return "TIME";
    }

    /**
     * Get {@link SqlType#SHORT} type present.
     *
     * @param def target attr def
     * @return type sql present
     */
    @SuppressWarnings("PMD")
    private String getShortTypeSql(AttributeDefinition def) {
        return "SHORT";
    }

    /**
     * Get {@link SqlType#LONG} type present.
     *
     * @param def target attr def
     * @return type sql present
     */
    @SuppressWarnings("PMD")
    private String getLongTypeSql(AttributeDefinition def) {
        return "BIGINT";
    }

    /**
     * Get {@link SqlType#INTEGER} type present.
     *
     * @param def target attr def
     * @return type sql present
     */
    @SuppressWarnings("PMD")
    private String getIntegerTypeSql(AttributeDefinition def) {
        return "INTEGER";
    }

    /**
     * Get {@link SqlType#FLOAT} type present.
     *
     * @param def target attr def
     * @return type sql present
     */
    @SuppressWarnings("PMD")
    private String getFloatTypeSql(AttributeDefinition def) {
        return "FLOAT";
    }

    /**
     * Get {@link SqlType#DOUBLE} type present.
     *
     * @param def target attr def
     * @return type sql present
     */
    @SuppressWarnings("PMD")
    private String getDoubleTypeSql(AttributeDefinition def) {
        return "DOUBLE";
    }

    /**
     * Get {@link SqlType#DATE} type present.
     *
     * @param def target attr def
     * @return type sql present
     */
    @SuppressWarnings("PMD")
    private String getDateTypeSql(AttributeDefinition def) {
        return "DATE";
    }

    /**
     * Get {@link SqlType#BYTE} type present.
     *
     * @param def target attr def
     * @return type sql present
     */
    @SuppressWarnings("PMD")
    private String getByteTypeSql(AttributeDefinition def) {
        return "BYTE";
    }

    /**
     * Get {@link SqlType#BOOLEAN} type present.
     *
     * @param def target attr def
     * @return type sql present
     */
    @SuppressWarnings("PMD")
    protected String getBooleanTypeSql(AttributeDefinition def) {
        return "BOOLEAN";
    }

    /**
     * Get {@link SqlType#STRING} type present.
     *
     * @param def target attr def
     * @return type sql present
     */
    @SuppressWarnings("PMD")
    protected String getStringTypeSql(AttributeDefinition def) {
        return "VARCHAR" + "(" + def.getLength() + ")";
    }

    @Override
    public String getEntityNameEscape() {
        return "`";
    }

    @Override
    public String getValueEscape() {
        return "'";
    }

    @Override
    public boolean supportTableForeignConstraint() {
        return true;
    }

    @Override
    public boolean supportTableUniqueConstraint() {
        return true;
    }
}
