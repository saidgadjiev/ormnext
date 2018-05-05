package ru.saidgadjiev.ormnext.core.criteria.api;

import ru.saidgadjiev.ormnext.core.query.core.condition.Condition;

public interface Criterion {

    Condition getCondition();

    Object[] getArgs();
}
