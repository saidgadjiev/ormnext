package ru.saidgadjiev.ormnext.core.query.core.constraints.table;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * Table foreign key constraint.
 */
public class ForeignKeyConstraint implements TableConstraint {

    /**
     * Column name.
     */
    private String columnName;

    /**
     * Foreign table name.
     */
    private String tableName;

    /**
     * Foreign column name from {@link ForeignKeyConstraint#tableName}.
     */
    private String foreignColumnName;

    /**
     * Create new instance.
     * @param tableName target foreign table name
     * @param foreignColumnName target foreign column name
     * @param columnName target column name
     */
    public ForeignKeyConstraint(String tableName, String foreignColumnName, String columnName) {
        this.tableName = tableName;
        this.foreignColumnName = foreignColumnName;
        this.columnName = columnName;
    }

    /**
     * Return current foreign table name.
     * @return tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Return current foreign column name.
     * @return foreignColumnName
     */
    public String getForeignColumnName() {
        return foreignColumnName;
    }

    /**
     * Return current columnName.
     * @return columnName
     */
    public String getColumnName() {
        return columnName;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
