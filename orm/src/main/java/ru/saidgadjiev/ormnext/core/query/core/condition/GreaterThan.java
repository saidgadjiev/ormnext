package ru.saidgadjiev.ormnext.core.query.core.condition;

import ru.saidgadjiev.ormnext.core.query.core.Operand;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * This class represent > restriction.
 */
public class GreaterThan implements Condition {

    /**
     * Left checked value.
     * @see Operand
     */
    private final Operand first;

    /**
     * Right checked value.
     * @see Operand
     */
    private final Operand second;

    /**
     * Create new instance.
     * @param first target left checked value.
     * @param second taget right checked value.
     */
    public GreaterThan(Operand first, Operand second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Return current left checked value.
     * @return first
     */
    public Operand getFirst() {
        return first;
    }

    /**
     * Return current right checked value.
     * @return second
     */
    public Operand getSecond() {
        return second;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            first.accept(visitor);
            second.accept(visitor);
        }
    }
}
