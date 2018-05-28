package ru.saidgadjiev.ormnext.core.query.visitor.element.common;

import ru.saidgadjiev.ormnext.core.query.visitor.element.literals.RValue;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * This class present update value eg. name = 'test'.
 */
public class UpdateValue implements QueryElement {

    /**
     * Update column name.
     */
    private final String name;

    /**
     * Value.
     * @see RValue
     */
    private RValue value;

    /**
     * Create new instance with requested {@code name} and {@code value}.
     * @param name target column name
     * @param value target value
     */
    public UpdateValue(String name, RValue value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Return current column name.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Return current value.
     * @return value
     */
    public RValue getValue() {
        return value;
    }

    /**
     * Provide update value.
     * @param value значение
     * @see RValue
     */
    public void setValue(RValue value) {
        this.value = value;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            value.accept(visitor);
        }
    }
}
