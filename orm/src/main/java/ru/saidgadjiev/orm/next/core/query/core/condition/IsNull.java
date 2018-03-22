package ru.saidgadjiev.orm.next.core.query.core.condition;

import ru.saidgadjiev.orm.next.core.query.core.Operand;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

public class IsNull implements Condition {

    private final Operand operand;

    public IsNull(Operand operand) {
        this.operand = operand;
    }

    public Operand getOperand() {
        return this.operand;
    }

    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
