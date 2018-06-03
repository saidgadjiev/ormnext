package ru.saidgadjiev.ormnext.core.query.visitor.element.condition;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.Operand;
import ru.saidgadjiev.ormnext.core.query.visitor.element.SelectQuery;

/**
 * This class represent in restriction.
 */
public class InSelect implements Condition {

    /**
     * Checked operand.
     * @see Operand
     */
    private Operand operand;

    /**
     * In sub query.
     * @see SelectQuery
     */
    private SelectQuery selectQuery;

    /**
     * Create new in.
     * @param selectQuery target sub query
     * @param operand target checked operand
     */
    public InSelect(SelectQuery selectQuery, Operand operand) {
        this.selectQuery = selectQuery;
        this.operand = operand;
    }

    /**
     * Return current in sub query.
     * @return selectQuery
     */
    public SelectQuery getSelectQuery() {
        return selectQuery;
    }

    /**
     * Return current checked operand.
     * @return operand
     */
    public Operand getOperand() {
        return operand;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            operand.accept(visitor);
            selectQuery.accept(visitor);
        }
    }
}
