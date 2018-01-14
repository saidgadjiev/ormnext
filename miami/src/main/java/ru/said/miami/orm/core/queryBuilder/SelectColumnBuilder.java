package ru.said.miami.orm.core.queryBuilder;

import ru.said.miami.orm.core.query.core.columnSpec.ColumnSpec;
import ru.said.miami.orm.core.query.core.columnSpec.DisplayedColumnSpec;
import ru.said.miami.orm.core.query.core.columnSpec.DisplayedColumns;

@SuppressWarnings("PMD")
public class SelectColumnBuilder {

    private String name;

    public SelectColumnBuilder name(String name) {
        this.name = name;

        return this;
    }

    public DisplayedColumnSpec build() {
        return new DisplayedColumns(new ColumnSpec(name));
    }
}
