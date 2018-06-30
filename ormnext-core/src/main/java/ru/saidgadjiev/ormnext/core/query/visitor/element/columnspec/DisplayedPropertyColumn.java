package ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * This class represent simple select column.
 *
 * @author Said Gadjiev
 */
public class DisplayedPropertyColumn extends DisplayedColumnSpec {

    /**
     * True if this element was resolved.
     */
    private boolean resolved;

    /**
     * SelectQuery column.
     * @see ColumnSpec
     */
    private PropertyColumnSpec columnSpec;

    /**
     * Create new instance with provided column.
     * @param columnSpec target column item
     */
    public DisplayedPropertyColumn(PropertyColumnSpec columnSpec) {
        this.columnSpec = columnSpec;
    }

    /**
     * Return current column.
     * @return columnspec
     */
    public PropertyColumnSpec getColumnSpec() {
        return columnSpec;
    }

    /**
     * Is resolved?
     *
     * @return is resolved?
     */
    public boolean isResolved() {
        return resolved;
    }

    /**
     * Set resolved for this element.
     */
    public void setResolved() {
        this.resolved = true;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            columnSpec.accept(visitor);
        }
    }
}
