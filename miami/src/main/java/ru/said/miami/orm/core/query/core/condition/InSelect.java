package ru.said.miami.orm.core.query.core.condition;

import ru.said.miami.orm.core.query.core.Operand;
import ru.said.miami.orm.core.query.core.Select;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

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
        visitor.start(this);
        visitor.finish(this);
    }

    public Operand getOperand() {
        return operand;
    }
}
