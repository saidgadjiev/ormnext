package ru.saidgadjiev.ormnext.core.query.core.clause;

import ru.saidgadjiev.ormnext.core.query.core.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

import java.util.*;

/**
 * This class represent order by item clause.
 */
public class OrderByItem implements QueryElement {

    /**
     * Order asc. Default use ASC order. Provide false for DESC order.
     */
    private boolean asc = true;

    /**
     * Order by column list.
     * @see ColumnSpec
     */
    private final List<ColumnSpec> columns;

    /**
     * Create new order by with provided order direction and columns. If asc is true sort will be ASC else DESC.
     * @param asc target sort direction
     * @param columns target order by columns
     */
    public OrderByItem(boolean asc, ColumnSpec ... columns) {
        this(columns);
        this.asc = asc;
    }

    /**
     * Create new order by with provided columns. Use sort direction ASC by default.
     * @param columns target order by columns
     */
    public OrderByItem(ColumnSpec ... columns) {
        this.columns = Arrays.asList(columns);
    }

    /**
     * Return order columns.
     * @return order columns
     */
    public List<ColumnSpec> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    /**
     * Add new order column.
     * @param column new column
     */
    public void addColumn(ColumnSpec column) {
        columns.add(column);
    }

    /**
     * Return is sort order ASC.
     * @return asc
     */
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
