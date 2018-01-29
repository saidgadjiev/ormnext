package ru.said.orm.next.core.query.core.column_spec;

import ru.said.orm.next.core.query.core.Operand;
import ru.said.orm.next.core.query.visitor.QueryVisitor;

public class DisplayedOperand extends DisplayedColumnSpec {

    private Operand operand;

    public DisplayedOperand(Operand operand) {
        this.operand = operand;
    }

    public Operand getOperand() {
        return operand;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        operand.accept(visitor);
        visitor.finish(this);
    }
}
