package ru.saidgadjiev.ormnext.core.query.visitor.element.condition;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.Operand;

/**
 * This class represent less than restriction.
 *
 * @author said gadjiev
 */
public class LessThan implements Condition {

    /**
     * Left checked value.
     * @see Operand
     */
    private final Operand first;

    /**
     * Left checked value.
     * @see Operand
     */
    private final Operand second;

    /**
     * Create a new instance.
     * @param first target left checked value.
     * @param second taget right checked value.
     */
    public LessThan(Operand first, Operand second) {
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
