package ru.said.miami.orm.core.query.core.clause.from;

import ru.said.miami.orm.core.query.core.common.TableRef;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

public class FromTable implements FromExpression {

    private TableRef tableRef;

    public FromTable(TableRef tableRef) {
        this.tableRef = tableRef;
    }

    public TableRef getTableRef() {
        return tableRef;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        tableRef.accept(visitor);
        visitor.finish(this);
    }
}
