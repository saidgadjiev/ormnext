package ru.said.orm.next.core.query.core.condition;

import ru.said.orm.next.core.query.core.Operand;
import ru.said.orm.next.core.query.visitor.QueryVisitor;
import ru.said.orm.next.core.query.core.Operand;
import ru.said.orm.next.core.query.visitor.QueryVisitor;

public class LessThan implements Condition {

    private final Operand first;
    private final Operand second;

    public LessThan(Operand first, Operand second) {
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
