package ru.said.miami.orm.core.query.core.columnSpec;

import ru.said.miami.orm.core.query.core.Alias;
import ru.said.miami.orm.core.query.core.Operand;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

public class DisplayedOperand implements DisplayedColumnSpec {

    private Alias alias;

    private Operand operand;

    public Alias getAlias() {
        return alias;
    }

    public void alias(Alias alias) {
        this.alias = alias;
    }

    public Operand getOperand() {
        return operand;
    }

    public void setOperand(Operand operand) {
        this.operand = operand;
    }

    @Override
    public void accept(QueryVisitor visitor) {

    }
}
