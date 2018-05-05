package ru.saidgadjiev.ormnext.core.query.core.condition;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

public class Not implements Condition {

    private final Condition condition;

    public Not(Condition condition) {
        this.condition = condition;
    }

    public Condition getCondition() {
        return this.condition;
    }

    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            condition.accept(visitor);
        }
    }
}
