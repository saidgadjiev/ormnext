package ru.saidgadjiev.ormnext.core.query.criteria.impl;

import ru.saidgadjiev.ormnext.core.query.visitor.element.clause.OrderByItem;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.PropertyColumnSpec;

/**
 * This class has static method for create order by item.
 *
 * @author Said Gadjiev
 */
public final class Order {

    /**
     * Can't be instantiated.
     */
    private Order() { }

    /**
     * Create new order by item with sort order ASC.
     * @param properties property names
     * @return new order by item
     */
    public static OrderByItem asc(String ... properties) {
        OrderByItem orderByItem = new OrderByItem();

        for (String property : properties) {
            orderByItem.addColumn(new PropertyColumnSpec(property));
        }

        return orderByItem;
    }

    /**
     * Create new order by item with sort order DESC.
     * @param properties property names
     * @return new order by item
     */
    public static OrderByItem desc(String ... properties) {
        OrderByItem orderByItem = new OrderByItem(false);

        for (String property : properties) {
            orderByItem.addColumn(new PropertyColumnSpec(property));
        }

        return orderByItem;
    }
}
