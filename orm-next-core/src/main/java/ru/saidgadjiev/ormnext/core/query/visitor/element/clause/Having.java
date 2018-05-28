package ru.saidgadjiev.ormnext.core.query.visitor.element.clause;

import ru.saidgadjiev.ormnext.core.query.visitor.element.condition.Expression;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * This class represent havig clause.
 */
public class Having implements QueryElement {

    /**
     * Having expression.
     * @see Expression
     */
    private Expression expression;

    /**
     * Create new instance with provided expression.
     * @param expression target expression
     */
    public Having(Expression expression) {
        this.expression = expression;
    }

    /**
     * Return current expression.
     * @return expression
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * Provide new having expression.
     * @param expression target expression
     */
    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            expression.accept(visitor);
        }
    }
}
