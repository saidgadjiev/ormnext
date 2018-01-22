package ru.said.orm.next.core.query.core.join;

import ru.said.orm.next.core.query.core.common.TableRef;
import ru.said.orm.next.core.query.core.condition.Expression;
import ru.said.orm.next.core.query.visitor.QueryElement;
import ru.said.orm.next.core.query.visitor.QueryVisitor;

public class JoinInfo implements QueryElement {

    private TableRef tableRef;

    private Expression expression = new Expression();

    public JoinInfo(TableRef tableRef) {
        this.tableRef = tableRef;
    }

    public TableRef getTableRef() {
        return tableRef;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        expression.accept(visitor);
        visitor.finish(this);
    }
}
