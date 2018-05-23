package ru.saidgadjiev.ormnext.core.criteria.impl;

import ru.saidgadjiev.ormnext.core.query.core.clause.OrderByItem;
import ru.saidgadjiev.ormnext.core.query.core.columnspec.ColumnSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 18.03.2018.
 */
public class OrderBy {

    public ru.saidgadjiev.ormnext.core.query.core.clause.OrderBy orderBy = new ru.saidgadjiev.ormnext.core.query.core.clause.OrderBy();

    public OrderBy add(boolean asc, String ... columns) {
        List<ColumnSpec> columnSpecs = new ArrayList<>();

        for (String column: columns) {
            columnSpecs.add(new ColumnSpec(column));
        }

        orderBy.add(new OrderByItem(asc, columnSpecs));

        return this;
    }

    public ru.saidgadjiev.ormnext.core.query.core.clause.OrderBy create() {
        return orderBy;
    }
}
