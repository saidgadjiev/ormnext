package ru.saidgadjiev.ormnext.core.query.core.clause;

import ru.saidgadjiev.ormnext.core.query.core.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OrderByItem implements QueryElement {

    private boolean asc;

    private final List<ColumnSpec> columns = new ArrayList<>();

    public OrderByItem(boolean asc, Collection<ColumnSpec> columns) {
        this.asc = asc;
        this.columns.addAll(columns);
    }

    public List<ColumnSpec> getColumns() {
        return columns;
    }

    public void add(ColumnSpec column) {
        columns.add(column);
    }

    public boolean isAsc() {
        return asc;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            columns.forEach(columnSpec -> columnSpec.accept(visitor));
        }
    }

}
