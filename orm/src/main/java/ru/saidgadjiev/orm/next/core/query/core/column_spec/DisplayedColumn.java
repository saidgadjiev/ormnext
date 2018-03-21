package ru.saidgadjiev.orm.next.core.query.core.column_spec;

import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

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
        visitor.visit(this);
    }
}
