package ru.saidgadjiev.ormnext.core.query.core.condition;

import ru.saidgadjiev.ormnext.core.query.core.Operand;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * This class represent is null restriction.
 */
public class IsNull implements Condition {

    /**
     * Checked operand.
     * @see Operand
     */
    private final Operand operand;

    /**
     * Create new instance.
     * @param operand target checked operand
     */
    public IsNull(Operand operand) {
        this.operand = operand;
    }

    /**
     * Return current checked operand.
     * @return operand
     */
    public Operand getOperand() {
        return operand;
    }

    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            operand.accept(visitor);
        }
    }
}
