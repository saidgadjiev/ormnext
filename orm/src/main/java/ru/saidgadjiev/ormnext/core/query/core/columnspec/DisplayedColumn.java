package ru.saidgadjiev.ormnext.core.query.core.columnspec;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

public class DisplayedColumn extends DisplayedColumnSpec {

    private ColumnSpec columnSpec;

    public DisplayedColumn(ColumnSpec columnSpec) {
        this.columnSpec = columnSpec;
    }

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
