package ru.saidgadjiev.orm.next.core.criteria.impl;

import ru.saidgadjiev.orm.next.core.query.core.clause.OrderBy;
import ru.saidgadjiev.orm.next.core.query.core.clause.OrderByItem;

/**
 * Created by said on 18.03.2018.
 */
public class OrderByList {

    public OrderBy orderBy = new OrderBy();

    public OrderByList add(OrderByItem orderByItem) {
        orderBy.add(orderByItem);

        return this;
    }

    public OrderBy create() {
        return orderBy;
    }
}
