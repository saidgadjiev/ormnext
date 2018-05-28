package ru.saidgadjiev.ormnext.core.query_element.literals;

import ru.saidgadjiev.ormnext.core.loader.visitor.QueryVisitor;

/**
 * Int literal.
 */
public class IntLiteral implements Literal<Integer> {

    /**
     * Current value.
     */
    private final Integer value;

    /**
     * Create a new instance.
     * @param value target value
     */
    public IntLiteral(Integer value) {
        this.value = value;
    }

    @Override
    public String getOriginal() {
        return String.valueOf(value);
    }

    @Override
    public Integer get() {
        return value;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);

    }
}
