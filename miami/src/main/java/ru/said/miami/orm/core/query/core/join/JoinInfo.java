package ru.said.miami.orm.core.query.core.join;

import ru.said.miami.orm.core.query.core.common.TableRef;
import ru.said.miami.orm.core.query.core.condition.Expression;
import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

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
