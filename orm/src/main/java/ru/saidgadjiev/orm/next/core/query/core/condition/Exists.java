package ru.saidgadjiev.orm.next.core.query.core.condition;

import ru.saidgadjiev.orm.next.core.query.core.Alias;
import ru.saidgadjiev.orm.next.core.query.core.Operand;
import ru.saidgadjiev.orm.next.core.query.core.Select;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

public class Exists implements Condition {

    private Select select;

    private Operand operand;

    public Exists(Select select, Operand operand) {
        this.select = select;
        this.operand = operand;
    }

    public Select getSelect() {
        return select;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);

    }

    public Operand getOperand() {
        return operand;
    }
}
