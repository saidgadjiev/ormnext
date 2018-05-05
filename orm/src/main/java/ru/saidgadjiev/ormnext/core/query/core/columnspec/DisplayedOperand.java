package ru.saidgadjiev.ormnext.core.query.core.columnspec;

import ru.saidgadjiev.ormnext.core.query.core.Operand;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.core.Operand;

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
        if (visitor.visit(this)) {
            operand.accept(visitor);
        }
    }
}
