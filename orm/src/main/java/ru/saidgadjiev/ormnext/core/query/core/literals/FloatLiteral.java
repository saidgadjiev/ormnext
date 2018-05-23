package ru.saidgadjiev.ormnext.core.query.core.literals;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * Float literal.
 */
public class FloatLiteral implements Literal<Float> {

    /**
     * Current value.
     */
    private final float value;

    /**
     * Create new instance.
     * @param value target value
     */
    public FloatLiteral(float value) {
        this.value = value;
    }

    @Override
    public String getOriginal() {
        return String.valueOf(value);
    }

    @Override
    public Float get() {
        return value;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);

    }
}
