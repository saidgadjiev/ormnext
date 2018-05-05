package ru.saidgadjiev.ormnext.core.query.core.clause;

import ru.saidgadjiev.ormnext.core.query.core.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * Created by said on 18.11.17.
 */
public class GroupByItem implements QueryElement {

    private ColumnSpec columnSpec;

    public GroupByItem(ColumnSpec columnSpec) {
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
