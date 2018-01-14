package ru.said.miami.orm.core.query.core.join;

import ru.said.miami.orm.core.query.core.common.TableRef;
import ru.said.miami.orm.core.query.core.condition.Expression;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

public class LeftJoin implements JoinExpression {

    private TableRef joinedTableRef;

    private Expression expression = new Expression();

    public LeftJoin(TableRef joinedTableRef) {
        this.joinedTableRef = joinedTableRef;
    }

    public LeftJoin(TableRef joinedTableRef, Expression expression) {
        this.joinedTableRef = joinedTableRef;
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public TableRef getJoinedTableRef() {
        return joinedTableRef;
    }

    public void setJoinedTableRef(TableRef joinedTableRef) {
        this.joinedTableRef = joinedTableRef;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        expression.accept(visitor);
        visitor.finish(this);
    }

}
