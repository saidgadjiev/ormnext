package ru.saidgadjiev.ormnext.core.query.visitor.element.condition;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.Operand;
import ru.saidgadjiev.ormnext.core.query.visitor.element.literals.RValue;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represent in (value1, value2, ...) restriction.
 *
 * @author Said Gadjiev
 */
public class InValues implements Condition {

    /**
     * Checked operand.
     * @see Operand
     */
    private final Operand operand;

    /**
     * In values list.
     * @see RValue
     */
    private final List<RValue> values = new ArrayList<>();

    /**
     * Create a new instance.
     * @param operand target checked operand
     */
    public InValues(Operand operand) {
        this.operand = operand;
    }

    /**
     * Add new value to in values list.
     * @param value target value
     * @see RValue
     */
    public void addValue(RValue value) {
        this.values.add(value);
    }

    /**
     * Get in values list.
     * @return values
     * @see RValue
     */
    public List<RValue> getValues() {
        return values;
    }

    /**
     * Get current checked operand.
     * @return operand
     * @see Operand
     */
    public Operand getOperand() {
        return operand;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            operand.accept(visitor);
            values.forEach(value -> value.accept(visitor));
        }
    }
}
