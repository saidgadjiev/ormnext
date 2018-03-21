package ru.saidgadjiev.orm.next.core.criteria.impl;

import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectColumnsList;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.DisplayedColumnSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 18.03.2018.
 */
public class ProjectionList {

    private List<DisplayedColumnSpec> displayedColumns = new ArrayList<>();

    public ProjectionList add(DisplayedColumnSpec displayedColumnSpec) {
        displayedColumns.add(displayedColumnSpec);

        return this;
    }

    public SelectColumnsList create() {
        SelectColumnsList selectColumnsList = new SelectColumnsList();

        selectColumnsList.addAll(displayedColumns);

        return selectColumnsList;
    }
}
