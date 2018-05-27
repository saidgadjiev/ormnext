package ru.saidgadjiev.ormnext.core.query.core.function;

import ru.saidgadjiev.ormnext.core.query.core.condition.Expression;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * This class represent sql AVG.
 */
public class AVG implements Function {

    /**
     * AVG expression.
     * @see Expression
     */
    private final Expression expression;

    /**
     * Create new instance.
     * @param expression target expression
     */
    public AVG(Expression expression) {
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
