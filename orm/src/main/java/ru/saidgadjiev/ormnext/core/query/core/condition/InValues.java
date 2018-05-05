package ru.saidgadjiev.ormnext.core.query.core.condition;

import ru.saidgadjiev.ormnext.core.query.core.Operand;
import ru.saidgadjiev.ormnext.core.query.core.literals.RValue;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.List;

public class InValues implements Condition {

    private final List<RValue> values = new ArrayList<>();

    private final Operand operand;

    public InValues(Operand operand) {
        this.operand = operand;
    }

    public void addValue(RValue literal) {
        this.values.add(literal);
    }

    public List<RValue> getValues() {
        return this.values;
    }

    public Operand getOperand() {
        return this.operand;
    }

    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            operand.accept(visitor);
            values.forEach(value -> value.accept(visitor));
        }
    }
}
