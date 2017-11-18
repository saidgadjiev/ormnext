package ru.said.miami.orm.core.query.core.condition;

import ru.said.miami.orm.core.query.core.Operand;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

public class GreaterThanOrEquals implements Condition {

    private final Operand first;
    private final Operand second;

    public GreaterThanOrEquals(Operand first, Operand second) {
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
        visitor.start(this);
        visitor.finish(this);
    }
}
