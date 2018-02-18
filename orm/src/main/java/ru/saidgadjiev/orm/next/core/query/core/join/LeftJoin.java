package ru.saidgadjiev.orm.next.core.query.core.join;

import ru.saidgadjiev.orm.next.core.query.core.common.TableRef;
import ru.saidgadjiev.orm.next.core.query.core.condition.Expression;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

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
        visitor.visit(this, visitor);
        expression.accept(visitor);

    }

}
