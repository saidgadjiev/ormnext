package ru.saidgadjiev.ormnext.core.criteria.impl;

import ru.saidgadjiev.ormnext.core.query.core.clause.GroupByItem;
import ru.saidgadjiev.ormnext.core.query.core.columnspec.ColumnSpec;

/**
 * This class has static method for create group by item.
 */
public final class Group {

    /**
     * Can't be instantiated.
     */
    private Group() { }

    /**
     * Create new group by item by property.
     * @param property target property.
     * @return new group by item
     */
    public static GroupByItem groupBy(String property) {
        return new GroupByItem(new ColumnSpec(property));
    }
}
