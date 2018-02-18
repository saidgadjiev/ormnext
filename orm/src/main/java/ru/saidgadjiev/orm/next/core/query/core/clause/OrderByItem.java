package ru.saidgadjiev.orm.next.core.query.core.clause;

import ru.saidgadjiev.orm.next.core.query.core.column_spec.ColumnSpec;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class OrderByItem implements QueryElement {

    private boolean asc = true;

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
        visitor.visit(this, visitor);

    }
}
