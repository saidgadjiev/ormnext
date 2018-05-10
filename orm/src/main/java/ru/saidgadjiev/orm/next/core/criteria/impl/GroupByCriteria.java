package ru.saidgadjiev.orm.next.core.criteria.impl;

import ru.saidgadjiev.orm.next.core.query.core.clause.GroupBy;
import ru.saidgadjiev.orm.next.core.query.core.clause.GroupByItem;
import ru.saidgadjiev.orm.next.core.query.core.columnspec.ColumnSpec;

public class GroupByCriteria {

    private GroupBy groupBy = new GroupBy();

    public GroupByCriteria add(String propertyName) {
        groupBy.add(new GroupByItem(new ColumnSpec(propertyName)));

        return this;
    }

    public GroupBy create() {
        return groupBy;
    }
}
