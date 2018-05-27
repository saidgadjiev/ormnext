package ru.saidgadjiev.ormnext.core.query.core.condition;

import ru.saidgadjiev.ormnext.core.query.core.Operand;
import ru.saidgadjiev.ormnext.core.query.core.Select;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * This class represent not in restriction.
 */
public class NotInSelect implements Condition {

    /**
     * Checked operand.
     * @see Operand
     */
    private Operand operand;

    /**
     * Not in sub query.
     * @see Select
     */
    private Select select;

    /**
     * Create new not in.
     * @param select target sub query
     * @param operand target checked operand
     */
    public NotInSelect(Select select, Operand operand) {
        this.select = select;
        this.operand = operand;
    }

    /**
     * Return current not in sub query.
     * @return select
     */
    public Select getSelect() {
        return select;
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
            select.accept(visitor);
        }
    }
}
