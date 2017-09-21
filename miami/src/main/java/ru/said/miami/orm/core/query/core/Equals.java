package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.query.visitor.QueryVisitor;

public class Equals implements Condition {

    private Operand first;

    private Operand second;

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        first.accept(visitor);
        second.accept(visitor);
        visitor.finish(this);
    }
}
