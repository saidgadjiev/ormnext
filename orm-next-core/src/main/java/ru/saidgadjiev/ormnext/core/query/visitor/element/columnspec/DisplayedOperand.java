package ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.Operand;

/**
 * This class represent select operand eg. aggregate functions
 *
 * @author said gadjiev
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
