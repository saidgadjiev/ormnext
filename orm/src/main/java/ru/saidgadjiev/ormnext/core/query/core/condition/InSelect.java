package ru.saidgadjiev.ormnext.core.query.core.condition;

import ru.saidgadjiev.ormnext.core.query.core.Operand;
import ru.saidgadjiev.ormnext.core.query.core.Select;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

public class InSelect implements Condition {

    private Select select;

    private Operand operand;

    public InSelect(Select select, Operand operand) {
        this.select = select;
        this.operand = operand;
    }

    public Select getSelect() {
        return select;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            operand.accept(visitor);
            select.accept(visitor);
        }
    }

    public Operand getOperand() {
        return operand;
    }
}
