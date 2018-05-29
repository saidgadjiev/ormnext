package ru.saidgadjiev.ormnext.core.field.field_type;

/**
 * Column key.
 *
 * @author said gadjiev
 */
public class ColumnKey {

    /**
     * Effective prefix.
     */
    private static final int HASH_CODE_PREFIX = 31;

    /**
     * Table name.
     */
    private final String tableName;

    /**
     * Column name.
     */
    private final String columnName;

    /**
     * Create a new instance.
     * @param tableName target table name
     * @param columnName target column name
     */
    public ColumnKey(String tableName, String columnName) {
        this.tableName = tableName;
        this.columnName = columnName;
    }

    /**
     * Return current table name.
     * @return current table name
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Return current column name.
     * @return current column name
     */
    public String getColumnName() {
        return columnName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ColumnKey that = (ColumnKey) o;

        if (!tableName.equals(that.tableName)) {
            return false;
        }

        return columnName.equals(that.columnName);
    }

    @Override
    public int hashCode() {
        int result = tableName.hashCode();

        result = HASH_CODE_PREFIX * result + columnName.hashCode();

        return result;
    }

    @Override
    public String toString() {
        return "ColumnKey{"
                + "tableName='" + tableName + '\''
                + ", columnName='" + columnName + '\''
                + '}';
    }
}
