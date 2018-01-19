package ru.said.orm.next.core.query.core.clause.select;

import ru.said.orm.next.core.query.core.column_spec.DisplayedColumnSpec;
import ru.said.orm.next.core.query.visitor.QueryVisitor;
import ru.said.orm.next.core.query.core.column_spec.DisplayedColumnSpec;
import ru.said.orm.next.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by said on 04.11.17.
 */
public class SelectColumnsList implements SelectColumnsStrategy {

    List<DisplayedColumnSpec> columns = new ArrayList<>();

    public void addColumn(DisplayedColumnSpec columnSpec) {
        columns.add(columnSpec);
    }

    public void addAll(Collection<DisplayedColumnSpec> columns) {
        this.columns.addAll(columns);
    }

    public List<DisplayedColumnSpec> getColumns() {
        return columns;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
