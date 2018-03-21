package ru.saidgadjiev.orm.next.core.criteria.impl;

import ru.saidgadjiev.orm.next.core.query.core.clause.OrderByItem;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.ColumnSpec;

import java.util.ArrayList;
import java.util.List;

public class Order {

    public static OrderByList orderBy() {
        return new OrderByList();
    }

    public static OrderByItem orderAsc(String ... columns) {
        List<ColumnSpec> columnSpecs = new ArrayList<>();

        for (String column: columns) {
            columnSpecs.add(new ColumnSpec(column));
        }

        return new OrderByItem(true, columnSpecs);
    }
}
