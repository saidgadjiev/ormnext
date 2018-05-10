package ru.saidgadjiev.orm.next.core.query.core.condition;

import ru.saidgadjiev.orm.next.core.query.core.Operand;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

public class NotNull implements Condition {

    private final Operand operand;

    public NotNull(Operand operand) {
        this.operand = operand;
    }

    public Operand getOperand() {
        return this.operand;
    }

    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            operand.accept(visitor);
        }
    }
}
