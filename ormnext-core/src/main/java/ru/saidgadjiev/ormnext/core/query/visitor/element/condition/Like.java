package ru.saidgadjiev.ormnext.core.query.visitor.element.condition;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.Operand;

/**
 * This class represent like restriction.
 *
 * @author said gadjiev
 */
public class Like implements Condition {

    /**
     * Checked value.
     * @see Operand
     */
    private final Operand operand;

    /**
     * Like pattern.
     */
    private final String pattern;

    /**
     * Create a new instance.
     * @param operand target checked value
     * @param pattern target like pattern
     */
    public Like(Operand operand, String pattern) {
        this.operand = operand;
        this.pattern = pattern;
    }

    /**
     * Return current operand checked value.
     * @return operand
     */
    public Operand getOperand() {
        return operand;
    }

    /**
     * Return current like pattern.
     * @return pattern
     */
    public String getPattern() {
        return pattern;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            operand.accept(visitor);
        }
    }
}
