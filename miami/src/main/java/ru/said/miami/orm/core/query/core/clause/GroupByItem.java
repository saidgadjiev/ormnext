package ru.said.miami.orm.core.query.core.clause;

import ru.said.miami.orm.core.query.core.columnSpec.ColumnSpec;
import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

/**
 * Created by said on 18.11.17.
 */
public class GroupByItem implements QueryElement {

    private ColumnSpec columnSpec;

    public GroupByItem(ColumnSpec columnSpec) {
        this.columnSpec = columnSpec;
    }

    public ColumnSpec getColumnSpec() {
        return columnSpec;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        columnSpec.accept(visitor);
    }
}
