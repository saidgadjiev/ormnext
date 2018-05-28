package ru.saidgadjiev.ormnext.core.query_element.condition;

import ru.saidgadjiev.ormnext.core.query_element.Operand;
import ru.saidgadjiev.ormnext.core.loader.visitor.QueryVisitor;

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
     * Create a new instance.
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

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            operand.accept(visitor);
        }
    }
}
