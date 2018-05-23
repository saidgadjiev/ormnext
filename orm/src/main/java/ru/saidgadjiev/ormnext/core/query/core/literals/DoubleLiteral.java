package ru.saidgadjiev.ormnext.core.query.core.literals;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * Double literal.
 */
public class DoubleLiteral implements Literal<Double> {

    /**
     * Current value.
     */
    private final double value;

    /**
     * Create new instance.
     * @param value target value
     */
    public DoubleLiteral(double value) {
        this.value = value;
    }

    @Override
    public String getOriginal() {
        return String.valueOf(value);
    }

    @Override
    public Double get() {
        return value;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);

    }
}
