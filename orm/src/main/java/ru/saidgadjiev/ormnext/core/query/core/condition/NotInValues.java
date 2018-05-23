package ru.saidgadjiev.ormnext.core.query.core.condition;

import ru.saidgadjiev.ormnext.core.query.core.Operand;
import ru.saidgadjiev.ormnext.core.query.core.literals.RValue;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represent not in (value1, value2, ...) restriction
 */
public class NotInValues implements Condition {

    /**
     * Checked operand.
     * @see Operand
     */
    private final Operand operand;

    /**
     * Not in values list.
     * @see RValue
     */
    private final List<RValue> values = new ArrayList<>();

    /**
     * Create new instance.
     * @param operand target checked operand
     */
    public NotInValues(Operand operand) {
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

    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            operand.accept(visitor);
            values.forEach(value -> value.accept(visitor));
        }
    }
}
