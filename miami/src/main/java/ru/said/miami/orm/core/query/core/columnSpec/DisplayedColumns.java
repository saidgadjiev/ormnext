package ru.said.miami.orm.core.query.core.columnSpec;

import ru.said.miami.orm.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DisplayedColumns implements DisplayedColumnSpec {

    private List<ColumnSpec> columns = new ArrayList<>();

    public void addColumn(ColumnSpec columnSpec) {
        columns.add(columnSpec);
    }

    public void addAll(Collection<ColumnSpec> columns) {
        this.columns.addAll(columns);
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
