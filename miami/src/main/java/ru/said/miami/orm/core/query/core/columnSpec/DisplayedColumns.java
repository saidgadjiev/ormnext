package ru.said.miami.orm.core.query.core.columnSpec;

import ru.said.miami.orm.core.query.core.Alias;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DisplayedColumns extends DisplayedColumnSpec {

    private ColumnSpec columnSpec;

    public DisplayedColumns(ColumnSpec columnSpec) {
        this.columnSpec = columnSpec;
    }

    public ColumnSpec getColumnSpec() {
        return columnSpec;
    }

    @Override
    public void setAlias(Alias alias) {
        columnSpec.alias(alias);
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
