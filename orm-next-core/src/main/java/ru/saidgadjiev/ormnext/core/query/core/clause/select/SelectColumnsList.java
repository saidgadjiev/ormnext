package ru.saidgadjiev.ormnext.core.query.core.clause.select;

import ru.saidgadjiev.ormnext.core.query.core.columnspec.DisplayedColumnSpec;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This implementation represent select column list or aggregate functions in select.
 */
public class SelectColumnsList implements SelectColumnsStrategy {

    /**
     * Contains select list eg. aggregate functions, columns....
     * @see DisplayedColumnSpec
     */
    private List<DisplayedColumnSpec> columns = new ArrayList<>();

    /**
     * Add new select list item.
     * @param columnSpec target select list item
     */
    public void addColumn(DisplayedColumnSpec columnSpec) {
        columns.add(columnSpec);
    }

    /**
     * Add all provided select list items.
     * @param columns target select list items
     */
    public void addAll(Collection<DisplayedColumnSpec> columns) {
        this.columns.addAll(columns);
    }

    /**
     * Return all select list items.
     * @return columns
     */
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
