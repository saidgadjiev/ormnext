package ru.saidgadjiev.orm.next.core.query.core.clause;

import ru.saidgadjiev.orm.next.core.query.core.Alias;
import ru.saidgadjiev.orm.next.core.query.core.condition.Expression;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

public class Having implements QueryElement {

    private Expression expression = new Expression();

    public Having() {
    }

    public Having(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }

}
