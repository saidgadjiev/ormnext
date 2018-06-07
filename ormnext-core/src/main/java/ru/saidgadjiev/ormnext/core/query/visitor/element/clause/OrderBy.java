package ru.saidgadjiev.ormnext.core.query.visitor.element.clause;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class represent order by clause.
 *
 * @author Said Gadjiev
 */
public class OrderBy implements QueryElement {

    /**
     * Contains order by items.
     * @see OrderByItem
     */
    private final List<OrderByItem> orderByItems = new ArrayList<>();

    /**
     * Return current order by items.
     * @return orderByItems
     */
    public List<OrderByItem> getOrderByItems() {
        return orderByItems;
    }

    /**
     * Add new order by item.
     * @param orderByItem target order by item
     */
    public void add(OrderByItem orderByItem) {
        orderByItems.add(orderByItem);
    }

    /**
     * Add all order by items.
     * @param orderByItems target order by items
     */
    public void addAll(Collection<OrderByItem> orderByItems) {
        this.orderByItems.addAll(orderByItems);
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            orderByItems.forEach(orderByItem -> orderByItem.accept(visitor));
        }
    }
}
