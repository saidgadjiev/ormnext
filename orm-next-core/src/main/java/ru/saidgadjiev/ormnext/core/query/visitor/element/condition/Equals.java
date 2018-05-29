package ru.saidgadjiev.ormnext.core.query.visitor.element.condition;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.Operand;

/**
 * This class represent equals restriction.
 */
public class Equals implements Condition {

    /**
     * Left checked value.
     * @see Operand
     */
    private Operand first;

    /**
     * Right checked value.
     * @see Operand
     */
    private Operand second;

    /**
     * Create new equals.
     * @param first left checked value
     * @param second right checked value
     */
    public Equals(Operand first, Operand second) {
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
