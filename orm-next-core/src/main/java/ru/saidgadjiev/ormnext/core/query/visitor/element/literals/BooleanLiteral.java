package ru.saidgadjiev.ormnext.core.query.visitor.element.literals;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * Boolean literal.
 */
public class BooleanLiteral implements Literal<Boolean> {

    /**
     * Current value.
     */
    private final boolean value;

    /**
     * Create a new instance.
     * @param value target value
     */
    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    @Override
    public String getOriginal() {
        return String.valueOf(value);
    }

    @Override
    public Boolean get() {
        return value;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);

    }
}
