package ru.saidgadjiev.ormnext.core.query.visitor.element.constraints.attribute;

import ru.saidgadjiev.ormnext.core.field.ReferenceAction;
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

    private final ReferenceAction onDelete;

    private final ReferenceAction onUpdate;

    /**
     * Create a new instance.
     * @param tableName target table name
     * @param columnName target column name
     */
    public ReferencesConstraint(String tableName,
                                String columnName,
                                ReferenceAction onDelete,
                                ReferenceAction onUpdate) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.onDelete = onDelete;
        this.onUpdate = onUpdate;
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

    public ReferenceAction getOnDelete() {
        return onDelete;
    }

    public ReferenceAction getOnUpdate() {
        return onUpdate;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
