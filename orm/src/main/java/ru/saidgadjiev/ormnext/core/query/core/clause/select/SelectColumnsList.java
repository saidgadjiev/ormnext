package ru.saidgadjiev.ormnext.core.query.core.clause.select;

import ru.saidgadjiev.ormnext.core.query.core.columnspec.DisplayedColumnSpec;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.core.columnspec.DisplayedColumnSpec;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by said on 04.11.17.
 */
public class SelectColumnsList implements SelectColumnsStrategy {

    private List<DisplayedColumnSpec> columns = new ArrayList<>();

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
        if (visitor.visit(this)) {
            columns.forEach(displayedColumnSpec -> displayedColumnSpec.accept(visitor));
        }
    }
}
