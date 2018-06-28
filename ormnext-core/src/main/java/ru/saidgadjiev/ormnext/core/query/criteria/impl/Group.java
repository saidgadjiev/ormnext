package ru.saidgadjiev.ormnext.core.query.criteria.impl;

import ru.saidgadjiev.ormnext.core.query.visitor.element.clause.GroupByItem;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.PropertyColumnSpec;

/**
 * This class has static method for create group by item.
 *
 * @author Said Gadjiev
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
        return new GroupByItem(new PropertyColumnSpec(property));
    }
}
