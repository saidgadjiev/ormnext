package ru.saidgadjiev.orm.next.core.query.core.column_spec;

import ru.saidgadjiev.orm.next.core.query.core.Alias;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

public class DisplayedColumns extends DisplayedColumnSpec {

    private ColumnSpec columnSpec;

    public DisplayedColumns(ColumnSpec columnSpec) {
        this.columnSpec = columnSpec;
    }

    public ColumnSpec getColumnSpec() {
        return columnSpec;
    }

    @Override
    public void setAlias(Alias alias) {
        columnSpec.alias(alias);
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
