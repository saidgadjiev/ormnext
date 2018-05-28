package ru.saidgadjiev.ormnext.core.query_element.condition;

import ru.saidgadjiev.ormnext.core.query_element.Operand;
import ru.saidgadjiev.ormnext.core.query_element.literals.RValue;
import ru.saidgadjiev.ormnext.core.loader.visitor.QueryVisitor;

/**
 * This class represent between restriction.
 */
public class Between implements Condition {

    /**
     * Checked operand.
     */
    private final Operand operand;

    /**
     * Lower bound value.
     * @see RValue
     */
    private final RValue low;

    /**
     * Upper bound value.
     * @see RValue
     */
    private final RValue high;

    /**
     * Create new between.
     * @param operand target checked operand
     * @param low target lower bound
     * @param high target upper range
     */
    public Between(Operand operand, RValue low, RValue high) {
        this.operand = operand;
        this.low = low;
        this.high = high;
    }

    /**
     * Return current checked operand.
     * @return operand
     */
    public Operand getOperand() {
        return operand;
    }

    /**
     * Return current lower bound.
     * @return low
     */
    public RValue getLow() {
        return low;
    }

    /**
     * Return current upper bound.
     * @return high
     */
    public RValue getHigh() {
        return high;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            operand.accept(visitor);
            low.accept(visitor);
            high.accept(visitor);
        }
    }
}
