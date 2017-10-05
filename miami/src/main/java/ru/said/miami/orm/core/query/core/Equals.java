package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.query.visitor.QueryVisitor;

public class Equals implements Condition {

    private Operand first;

    private Operand second;

    public Equals(Operand first, Operand second) {
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
