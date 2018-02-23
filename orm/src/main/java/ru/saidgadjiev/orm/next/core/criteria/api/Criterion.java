package ru.saidgadjiev.orm.next.core.criteria.api;

import ru.saidgadjiev.orm.next.core.query.core.condition.Condition;

public interface Criterion {

    Condition getCondition();

    Object[] getArgs();
}
