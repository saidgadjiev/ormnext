package ru.said.miami.orm.core.query.core.columnSpec;

import ru.said.miami.orm.core.query.core.Alias;
import ru.said.miami.orm.core.query.core.Operand;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

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

    }
}
