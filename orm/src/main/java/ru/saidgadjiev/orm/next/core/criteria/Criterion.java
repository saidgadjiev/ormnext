package ru.saidgadjiev.orm.next.core.criteria;

import ru.saidgadjiev.orm.next.core.query.core.condition.Condition;

public class Criterion {

    private final Condition condition;

    private final Object value;

    public Criterion(Condition condition, Object value) {
        this.condition = condition;
        this.value = value;
    }

    public Condition getCondition() {
        return condition;
    }

    public Object getValue() {
        return value;
    }
}
