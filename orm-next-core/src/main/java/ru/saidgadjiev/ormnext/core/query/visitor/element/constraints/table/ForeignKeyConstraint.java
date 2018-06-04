package ru.saidgadjiev.ormnext.core.query.visitor.element.constraints.table;

import ru.saidgadjiev.ormnext.core.field.ReferenceAction;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * Table foreign key constraint.
 *
 * @author said gadjiev
 */
public class ForeignKeyConstraint implements TableConstraint {

    /**
     * Column name.
     */
    private String columnName;

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
     * Foreign table name.
     */
    private String tableName;

    /**
     * Foreign column name from {@link ForeignKeyConstraint#tableName}.
     */
    private String foreignColumnName;

    /**
     * Create a new instance.
     *
     * @param tableName         target foreign table name
     * @param foreignColumnName target foreign column name
     * @param columnName        target column name
     * @param onDelete          on delete action
     * @param onUpdate          on update action
     */
    public ForeignKeyConstraint(String tableName,
                                String foreignColumnName,
                                String columnName,
                                ReferenceAction onDelete,
                                ReferenceAction onUpdate) {
        this.tableName = tableName;
        this.foreignColumnName = foreignColumnName;
        this.columnName = columnName;
        this.onDelete = onDelete;
        this.onUpdate = onUpdate;
    }

    /**
     * Return current foreign table name.
     *
     * @return tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Return current foreign column name.
     *
     * @return foreignColumnName
     */
    public String getForeignColumnName() {
        return foreignColumnName;
    }

    /**
     * Return current columnName.
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
