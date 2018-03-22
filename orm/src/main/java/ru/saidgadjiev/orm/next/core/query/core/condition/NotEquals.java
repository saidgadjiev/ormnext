package ru.saidgadjiev.orm.next.core.query.core.condition;

import ru.saidgadjiev.orm.next.core.query.core.Operand;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

public class NotEquals implements Condition {

    private final Operand first;

    private final Operand second;

    public NotEquals(Operand first, Operand second) {
        this.first = first;
        this.second = second;
    }

    public Operand getFirst() {
        return this.first;
    }

    public Operand getSecond() {
        return this.second;
    }

    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }

}
