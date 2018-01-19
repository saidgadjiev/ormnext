package ru.said.orm.next.core.query.core.literals;

import ru.said.orm.next.core.query.core.Operand;
import ru.said.orm.next.core.query.visitor.QueryVisitor;
import ru.said.orm.next.core.query.core.Operand;
import ru.said.orm.next.core.query.visitor.QueryVisitor;

public class BooleanLiteral implements Operand, RValue {

    private boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
