package ru.saidgadjiev.ormnext.core.criteria.api;

import ru.saidgadjiev.ormnext.core.criteria.impl.CriterionArgument;
import ru.saidgadjiev.ormnext.core.query.core.condition.Condition;

/**
 * Implement this interface for create new criteria restriction.
 */
public interface Criterion {

    /**
     * Return condition.
     * @return condition
     * @see Condition
     */
    Condition getCondition();

    /**
     * Return condition arg.
     * @return condition arg
     * @see CriterionArgument
     */
    CriterionArgument getArg();
}
