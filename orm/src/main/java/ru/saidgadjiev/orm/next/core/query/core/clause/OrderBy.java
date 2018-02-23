package ru.saidgadjiev.orm.next.core.query.core.clause;

import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.List;

public class OrderBy implements QueryElement {

    private final List<OrderByItem> orderByItems = new ArrayList<>();

    public List<OrderByItem> getOrderByItems() {
        return orderByItems;
    }

    public void add(OrderByItem item) {
        orderByItems.add(item);
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
        for (OrderByItem item: orderByItems) {
            item.accept(visitor);
        }
    }
}
