package ru.saidgadjiev.ormnext.core.criteria.api;

import ru.saidgadjiev.ormnext.core.criteria.impl.CriterionArgument;
import ru.saidgadjiev.ormnext.core.query.core.condition.Condition;

import java.util.Map;

public interface Criterion {

    Condition getCondition();

    CriterionArgument getArg();
}
