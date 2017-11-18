package ru.said.miami.orm.core.query.core.function;

import ru.said.miami.orm.core.query.core.condition.Expression;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

public class MIN implements AgregateFunction {

    private final Expression expression;

    public MIN(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
