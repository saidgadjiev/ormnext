package ru.saidgadjiev.ormnext.core.query.criteria.api;

import ru.saidgadjiev.ormnext.core.query.criteria.impl.CriterionArgument;
import ru.saidgadjiev.ormnext.core.query.visitor.element.condition.Condition;

/**
 * Implement this interface for create new criteria restriction.
 *
 * @author Said Gadjiev
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
