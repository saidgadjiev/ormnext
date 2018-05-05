package ru.saidgadjiev.ormnext.core.query.core.clause;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OrderBy implements QueryElement {

    private final List<OrderByItem> orderByItems = new ArrayList<>();

    public List<OrderByItem> getOrderByItems() {
        return orderByItems;
    }


    public void addAll(Collection<OrderByItem> collection) {
        orderByItems.addAll(collection);
    }

    public void add(OrderByItem item) {
        orderByItems.add(item);
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            orderByItems.forEach(orderByItem -> orderByItem.accept(visitor));
        }
    }
}
