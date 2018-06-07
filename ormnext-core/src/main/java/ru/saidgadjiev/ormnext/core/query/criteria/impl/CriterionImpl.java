package ru.saidgadjiev.ormnext.core.query.criteria.impl;

import ru.saidgadjiev.ormnext.core.query.criteria.api.Criterion;
import ru.saidgadjiev.ormnext.core.query.visitor.element.condition.Condition;

/**
 * Criterion implement.
 *
 * @author Said Gadjiev
 */
public class CriterionImpl implements Criterion {

    /**
     * Condition.
     * @see Condition
     */
    private final Condition condition;

    /**
     * Property name.
     */
    private final String propertyName;

    /**
     * Arguments.
     */
    private final Object[] args;

    /**
     * Create a new instance.
     * @param condition target condition
     * @param propertyName target property
     * @param args target args
     */
    CriterionImpl(Condition condition, String propertyName, Object ... args) {
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
