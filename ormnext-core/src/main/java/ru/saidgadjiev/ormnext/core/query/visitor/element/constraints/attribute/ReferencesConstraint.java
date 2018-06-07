package ru.saidgadjiev.ormnext.core.query.visitor.element.constraints.attribute;

import ru.saidgadjiev.ormnext.core.field.ReferenceAction;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * Attribute references constraint.
 *
 * @author Said Gadjiev
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
     * On delete action.
     *
     * @see ReferenceAction
     */
    private final ReferenceAction onDelete;

    /**
     * On update action.
     *
     * @see ReferenceAction
     */
    private final ReferenceAction onUpdate;

    /**
     * Create a new instance.
     *
     * @param tableName  target table name
     * @param columnName target column name
     * @param onDelete   on delete action
     * @param onUpdate   on update action
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
     *
     * @return tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Return current column name.
     *
     * @return columnName
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Return on delete action.
     *
     * @return on delete action
     */
    public ReferenceAction getOnDelete() {
        return onDelete;
    }

    /**
     * Return on update action.
     *
     * @return on update action
     */
    public ReferenceAction getOnUpdate() {
        return onUpdate;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
