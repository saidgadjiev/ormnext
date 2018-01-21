package ru.said.orm.next.core.query.core.condition;

import ru.said.orm.next.core.query.core.Operand;
import ru.said.orm.next.core.query.core.Select;
import ru.said.orm.next.core.query.visitor.QueryVisitor;

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
        visitor.start(this);
        visitor.finish(this);
    }
}
