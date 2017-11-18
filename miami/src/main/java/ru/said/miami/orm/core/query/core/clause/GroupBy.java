package ru.said.miami.orm.core.query.core.clause;

import ru.said.miami.orm.core.query.core.columnSpec.ColumnSpec;
import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GroupBy implements QueryElement {

    private List<ColumnSpec> columns = new ArrayList<>();

    public void add(ColumnSpec columnSpec) {
        columns.add(columnSpec);
    }

    public void addAll(Collection<ColumnSpec> columnSpecs) {
        columns.addAll(columnSpecs);
    }

    public List<ColumnSpec> getColumns() {
        return columns;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
