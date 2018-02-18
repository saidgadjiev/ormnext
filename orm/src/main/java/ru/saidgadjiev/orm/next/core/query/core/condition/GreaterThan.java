package ru.saidgadjiev.orm.next.core.query.core.condition;

import ru.saidgadjiev.orm.next.core.query.core.Operand;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

public class GreaterThan implements Condition {

    private final Operand first;
    private final Operand second;

    public GreaterThan(Operand first, Operand second) {
        this.first = first;
        this.second = second;
    }

    public Operand getFirst() {
        return first;
    }

    public Operand getSecond() {
        return second;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);

    }
}
