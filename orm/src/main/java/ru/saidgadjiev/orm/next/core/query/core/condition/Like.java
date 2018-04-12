package ru.saidgadjiev.orm.next.core.query.core.condition;

import ru.saidgadjiev.orm.next.core.query.core.Operand;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

public class Like implements Condition {

    private final Operand operand;

    private final String pattern;

    public Like(Operand operand, String pattern) {
        this.operand = operand;
        this.pattern = pattern;
    }

    public Operand getOperand() {
        return this.operand;
    }

    public String getPattern() {
        return this.pattern;
    }

    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            operand.accept(visitor);
        }
    }
}
