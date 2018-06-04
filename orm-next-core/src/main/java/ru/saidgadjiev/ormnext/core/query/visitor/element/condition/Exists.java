package ru.saidgadjiev.ormnext.core.query.visitor.element.condition;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.Operand;
import ru.saidgadjiev.ormnext.core.query.visitor.element.SelectQuery;

/**
 * This class represent exists restriction.
 *
 * @author said gadjiev
 */
public class Exists implements Condition {

    /**
     * Checked operand.
     * @see Operand
     */
    private final Operand operand;

    /**
     * Exist sub query.
     * @see SelectQuery
     */
    private final SelectQuery selectQuery;

    /**
     * Create new exists.
     * @param selectQuery target sub query
     * @param operand target checked operand
     */
    public Exists(SelectQuery selectQuery, Operand operand) {
        this.selectQuery = selectQuery;
        this.operand = operand;
    }

    /**
     * Return current exist sub query.
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
