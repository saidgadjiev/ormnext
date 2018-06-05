package ru.saidgadjiev.ormnext.core.query.visitor.element.function;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.ColumnSpec;

/**
 * This class represent sql COUNT(column_name).
 *
 * @author said gadjiev
 */
public class CountColumn implements Function {

    /**
     * Count column.
     * @see ColumnSpec
     */
    private final ColumnSpec columnSpec;

    /**
     * Create a new instance.
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
