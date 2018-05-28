package ru.saidgadjiev.ormnext.core.query.criteria.impl;

import ru.saidgadjiev.ormnext.core.query.criteria.api.Criterion;
import ru.saidgadjiev.ormnext.core.query.visitor.element.AndCondition;
import ru.saidgadjiev.ormnext.core.query.visitor.element.condition.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represent restrictions container.
 */
public class Criteria {

    /**
     * Current expression.
     * @see Expression
     */
    private Expression where = new Expression();

    /**
     * Current restrictions group.
     * @see AndCondition
     */
    private AndCondition andCondition = new AndCondition();

    /**
     * Current restrictions args.
     * @see CriterionArgument
     */
    private List<CriterionArgument> args = new ArrayList<>();

    /**
     * Create a new instance.
     */
    public Criteria() {
        where.add(andCondition);
    }

    /**
     * Add new restriction.
     * @param criterion target restriction
     * @return this for chain
     */
    public Criteria add(Criterion criterion) {
        andCondition.add(criterion.getCondition());
        args.add(criterion.getArg());

        return this;
    }

    /**
     * Create new restrictions group which be separated by 'OR'.
     * @return this for chain
     */
    public Criteria or() {
        this.andCondition = new AndCondition();
        where.add(andCondition);

        return this;
    }

    /**
     * Return restrictions args.
     * @return restrictions args
     */
    public List<CriterionArgument> getArgs() {
        return args;
    }

    /**
     * Return current expression.
     * @return current expression
     */
    Expression expression() {
        return where;
    }

}
