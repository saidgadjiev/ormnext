package ru.saidgadjiev.ormnext.core.query.visitor.element.function;

import ru.saidgadjiev.ormnext.core.query.visitor.element.condition.Expression;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * This class represent sql MAX.
 */
public class MAX implements Function {

    /**
     * MAX expression.
     * @see Expression
     */
    private final Expression expression;

    /**
     * Create a new instance.
     * @param expression target expression
     */
    public MAX(Expression expression) {
        this.expression = expression;
    }

    /**
     * Return current expression.
     * @return expression
     */
    public Expression getExpression() {
        return expression;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            expression.accept(visitor);
        }
    }
}
