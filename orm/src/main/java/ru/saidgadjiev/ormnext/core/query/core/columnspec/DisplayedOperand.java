package ru.saidgadjiev.ormnext.core.query.core.columnspec;

import ru.saidgadjiev.ormnext.core.query.core.Operand;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * This class represent select operand eg. aggregate functions
 */
public class DisplayedOperand extends DisplayedColumnSpec {

    /**
     * Displayed operand.
     * @see Operand
     */
    private Operand operand;

    /**
     * Create new instance with provided operand.
     * @param operand target operand
     */
    public DisplayedOperand(Operand operand) {
        this.operand = operand;
    }

    /**
     * Return current displayed operand.
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
