package ru.saidgadjiev.ormnext.core.query.core.condition;

import ru.saidgadjiev.ormnext.core.query.core.Operand;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * This class represent <> restriction.
 */
public class NotEquals implements Condition {

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
     * Create new instance.
     * @param first target left checked value.
     * @param second taget right checked value.
     */
    public NotEquals(Operand first, Operand second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Return current left checked value.
     * @return first
     */
    public Operand getFirst() {
        return this.first;
    }

    /**
     * Return current right checked value.
     * @return second
     */
    public Operand getSecond() {
        return this.second;
    }

    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            first.accept(visitor);
            second.accept(visitor);
        }
    }

}
