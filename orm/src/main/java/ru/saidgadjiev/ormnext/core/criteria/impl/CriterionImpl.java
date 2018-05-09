package ru.saidgadjiev.ormnext.core.criteria.impl;

import ru.saidgadjiev.ormnext.core.criteria.api.Criterion;
import ru.saidgadjiev.ormnext.core.query.core.condition.Condition;

public class CriterionImpl implements Criterion {

    private Condition condition;

    private String propertyName;

    private Object[] args;

    public CriterionImpl(Condition condition, String propertyName, Object ... args) {
        this.condition = condition;
        this.propertyName = propertyName;
        this.args = args;
    }

    @Override
    public Condition getCondition() {
        return condition;
    }

    @Override
    public CriterionArgument getArg() {
        return new CriterionArgument(propertyName, args);
    }
}
