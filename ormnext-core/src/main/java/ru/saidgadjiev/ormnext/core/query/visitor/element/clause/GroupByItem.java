package ru.saidgadjiev.ormnext.core.query.visitor.element.clause;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.ColumnSpec;

/**
 * This class represent group by item.
 *
 * @author said gadjiev
 */
public class GroupByItem implements QueryElement {

    /**
     * Group by column spec.
     * @see ColumnSpec
     */
    private final ColumnSpec columnSpec;

    /**
     * Create group by item with provided column spec.
     * @param columnSpec target column spec
     */
    public GroupByItem(ColumnSpec columnSpec) {
        this.columnSpec = columnSpec;
    }

    /**
     * Return current column spec.
     * @return columnSpec
     */
    public ColumnSpec getColumnSpec() {
        return columnSpec;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            columnSpec.accept(visitor);
        }
    }
}
