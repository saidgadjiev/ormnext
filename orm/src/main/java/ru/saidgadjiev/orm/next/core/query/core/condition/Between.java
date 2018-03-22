package ru.saidgadjiev.orm.next.core.query.core.condition;

import ru.saidgadjiev.orm.next.core.query.core.Operand;
import ru.saidgadjiev.orm.next.core.query.core.literals.RValue;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

public class Between implements Condition {

    private final Operand operand;

    private final RValue low;

    private final RValue high;

    public Between(Operand operand, RValue low, RValue high) {
        this.operand = operand;
        this.low = low;
        this.high = high;
    }

    public Operand getOperand() {
        return operand;
    }

    public RValue getLow() {
        return low;
    }

    public RValue getHigh() {
        return high;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}