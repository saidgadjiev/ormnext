package ru.saidgadjiev.ormnext.core.field.field_type;

import java.util.List;

/**
 * Holds index info.
 *
 * @author said gadjiev
 */
public class IndexColumns {

    /**
     * Index columns.
     */
    private final List<String> columns;

    /**
     * Index name.
     */
    private final String name;

    /**
     * True if index is unique.
     */
    private final boolean unique;

    /**
     * Table name.
     */
    private final String tableName;

    /**
     * Create a new instance.
     * @param name target name
     * @param unique is unique index
     * @param tableName target table name
     * @param columns target columns
     */
    public IndexColumns(String name, boolean unique, String tableName, List<String> columns) {
        this.columns = columns;
        this.name = name;
        this.tableName = tableName;
        this.unique = unique;
    }

    /**
     * Return columns.
     * @return indexed columns
     */
    public List<String> getColumns() {
        return columns;
    }

    /**
     * Return index name.
     * @return index name
     */
    public String getName() {
        return name;
    }

    /**
     * Return table name.
     * @return table name
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * True if index is unique.
     * @return true if index is unique
     */
    public boolean isUnique() {
        return unique;
    }
}
