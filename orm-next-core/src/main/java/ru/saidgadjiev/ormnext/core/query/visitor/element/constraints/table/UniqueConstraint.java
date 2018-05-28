package ru.saidgadjiev.ormnext.core.query.visitor.element.constraints.table;

import ru.saidgadjiev.ormnext.core.field.field_type.UniqueColumns;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

import java.util.List;

/**
 * Unique table constraint.
 */
public class UniqueConstraint implements TableConstraint {

    /**
     * Unique column type.
     * @see UniqueColumns
     */
    private UniqueColumns uniqueColumns;

    /**
     * Create a new instance.
     * @param uniqueColumns target unique column type
     */
    public UniqueConstraint(UniqueColumns uniqueColumns) {
        this.uniqueColumns = uniqueColumns;
    }

    /**
     * Return current unique column names from {@link UniqueColumns#getColumns}.
     * @return current unique column names
     */
    public List<String> getUniqueColumnNames() {
        return uniqueColumns.getColumns();
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
