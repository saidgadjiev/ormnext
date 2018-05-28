package ru.saidgadjiev.ormnext.core.query.visitor.element.constraints.attribute;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * Attribute references constraint.
 */
public class ReferencesConstraint implements AttributeConstraint {

    /**
     * Target table name.
     */
    private final String tableName;

    /**
     * Target column name.
     */
    private final String columnName;

    /**
     * Create a new instance.
     * @param tableName target table name
     * @param columnName target column name
     */
    public ReferencesConstraint(String tableName, String columnName) {
        this.tableName = tableName;
        this.columnName = columnName;
    }

    /**
     * Return current table name.
     * @return tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Return current column name.
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
