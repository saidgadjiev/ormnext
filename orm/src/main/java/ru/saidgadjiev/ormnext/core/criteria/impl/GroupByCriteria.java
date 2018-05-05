package ru.saidgadjiev.ormnext.core.criteria.impl;

import ru.saidgadjiev.ormnext.core.query.core.clause.GroupBy;
import ru.saidgadjiev.ormnext.core.query.core.clause.GroupByItem;
import ru.saidgadjiev.ormnext.core.query.core.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.query.core.clause.GroupBy;
import ru.saidgadjiev.ormnext.core.query.core.clause.GroupByItem;

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
