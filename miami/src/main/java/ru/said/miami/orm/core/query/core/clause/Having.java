package ru.said.miami.orm.core.query.core.clause;

import ru.said.miami.orm.core.query.core.condition.Expression;
import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

public class Having implements QueryElement {

    private Expression expression = new Expression();

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        expression.accept(visitor);
        visitor.finish(this);
    }
}
