package ru.said.orm.next.core.query.core.clause.from;

import ru.said.orm.next.core.query.core.common.TableRef;
import ru.said.orm.next.core.query.visitor.QueryVisitor;
import ru.said.orm.next.core.query.visitor.QueryVisitor;

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
