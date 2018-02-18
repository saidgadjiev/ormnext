package ru.saidgadjiev.orm.next.core.query.core.condition;

import ru.saidgadjiev.orm.next.core.query.core.Operand;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

public class OperandCondition implements Condition {

    private Operand operand;

    public OperandCondition(Operand operand) {
        this.operand = operand;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this, visitor);

    }

    public Operand getOperand() {
        return operand;
    }
}
