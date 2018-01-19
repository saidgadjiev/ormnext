package ru.said.orm.next.core.query.core.literals;

import ru.said.orm.next.core.query.core.Operand;
import ru.said.orm.next.core.query.visitor.QueryVisitor;

import java.util.Date;

public class TimeLiteral implements Operand, RValue {

    private final Date time;

    public TimeLiteral(Date time) {
        this.time = time;
    }

    public Date get() {
        return this.time;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
