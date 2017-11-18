package ru.said.miami.orm.core.query.core.clause.select;

import ru.said.miami.orm.core.query.core.columnSpec.DisplayedColumnSpec;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

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
        for (DisplayedColumnSpec displayedColumnSpec: columns) {
            displayedColumnSpec.accept(visitor);
        }
        visitor.finish(this);
    }
}
