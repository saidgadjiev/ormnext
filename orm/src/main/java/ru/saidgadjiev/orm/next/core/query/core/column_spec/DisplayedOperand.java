package ru.saidgadjiev.orm.next.core.query.core.column_spec;

import ru.saidgadjiev.orm.next.core.query.core.Operand;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

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
        visitor.visit(this);
    }
}
