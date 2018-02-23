package ru.saidgadjiev.orm.next.core.query.core.function;

import ru.saidgadjiev.orm.next.core.query.core.condition.Expression;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

public class CountExpression implements Function {

    private final Expression expression;

    public CountExpression(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }

}
