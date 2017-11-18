package ru.said.miami.orm.core.query.core.condition;

import ru.said.miami.orm.core.query.core.Operand;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

public class OperandCondition implements Condition {

    private Operand operand;

    public OperandCondition(Operand operand) {
        this.operand = operand;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }

    public Operand getOperand() {
        return operand;
    }
}
