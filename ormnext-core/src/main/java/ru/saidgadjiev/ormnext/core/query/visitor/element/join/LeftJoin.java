package ru.saidgadjiev.ormnext.core.query.visitor.element.join;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.common.TableRef;
import ru.saidgadjiev.ormnext.core.query.visitor.element.condition.Expression;

/**
 * This class represent LEF JOIN.
 *
 * @author said gadjiev
 */
public class LeftJoin implements JoinExpression {

    /**
     * Joined table name.
     */
    private TableRef joinedTableRef;

    /**
     * Join expression.
     */
    private Expression expression = new Expression();

    /**
     * Create a new instance.
     * @param joinedTableRef target joined table name
     */
    public LeftJoin(TableRef joinedTableRef) {
        this.joinedTableRef = joinedTableRef;
    }

    /**
     * Create a new instance.
     * @param joinedTableRef target joined table name
     * @param expression target join expression
     */
    public LeftJoin(TableRef joinedTableRef, Expression expression) {
        this(joinedTableRef);
        this.expression = expression;
    }

    /**
     * Current join expression.
     * @return expression
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * Provide join expression.
     * @param expression target join expression
     */
    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    /**
     * Return current joined table name.
     * @return joinedTableRef
     */
    public TableRef getJoinedTableRef() {
        return joinedTableRef;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            joinedTableRef.accept(visitor);
            expression.accept(visitor);
        }
    }
}
