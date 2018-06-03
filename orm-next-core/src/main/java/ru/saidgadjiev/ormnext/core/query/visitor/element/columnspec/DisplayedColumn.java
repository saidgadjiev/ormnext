package ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * This class represent simple select column.
 */
public class DisplayedColumn extends DisplayedColumnSpec {

    /**
     * SelectQuery column.
     * @see ColumnSpec
     */
    private ColumnSpec columnSpec;

    /**
     * Create new instance with provided column.
     * @param columnSpec target column item
     */
    public DisplayedColumn(ColumnSpec columnSpec) {
        this.columnSpec = columnSpec;
    }

    /**
     * Return current column.
     * @return columnspec
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
