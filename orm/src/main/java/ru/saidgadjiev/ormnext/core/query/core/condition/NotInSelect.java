package ru.saidgadjiev.ormnext.core.query.core.condition;

import ru.saidgadjiev.ormnext.core.query.core.Operand;
import ru.saidgadjiev.ormnext.core.query.core.Select;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

public class NotInSelect implements Condition {

    private Select select;

    private Operand operand;

    public NotInSelect(Select select, Operand operand) {
        this.select = select;
        this.operand = operand;
    }

    public Select getSelect() {
        return select;
    }

    public Operand getOperand() {
        return operand;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            operand.accept(visitor);
            select.accept(visitor);
        }
    }
}
