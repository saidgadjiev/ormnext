package ru.saidgadjiev.ormnext.core.query_element.function;

import ru.saidgadjiev.ormnext.core.query_element.condition.Expression;
import ru.saidgadjiev.ormnext.core.loader.visitor.QueryVisitor;

/**
 * This class represent sql COUNT.
 */
public class CountExpression implements Function {

    /**
     * COUNT expression.
     * @see Expression
     */
    private final Expression expression;

    /**
     * Create a new instance.
     * @param expression target expression
     */
    public CountExpression(Expression expression) {
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
