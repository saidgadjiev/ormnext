package ru.saidgadjiev.ormnext.core.query.core.function;

import ru.saidgadjiev.ormnext.core.query.core.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * This class represent sql COUNT(column_name).
 */
public class CountColumn implements Function {

    /**
     * Count column.
     * @see ColumnSpec
     */
    private final ColumnSpec columnSpec;

    /**
     * Create new instance.
     * @param columnSpec target column.
     */
    public CountColumn(ColumnSpec columnSpec) {
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
