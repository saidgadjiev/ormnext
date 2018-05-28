package ru.saidgadjiev.ormnext.core.query.visitor.element.condition;

import ru.saidgadjiev.ormnext.core.query.visitor.element.Operand;
import ru.saidgadjiev.ormnext.core.query.visitor.element.Select;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

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
     * @see Select
     */
    private Select select;

    /**
     * Create new in.
     * @param select target sub query
     * @param operand target checked operand
     */
    public InSelect(Select select, Operand operand) {
        this.select = select;
        this.operand = operand;
    }

    /**
     * Return current in sub query.
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
