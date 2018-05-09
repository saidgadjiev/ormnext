package ru.saidgadjiev.ormnext.core.criteria.impl;

import ru.saidgadjiev.ormnext.core.criteria.api.Criterion;
import ru.saidgadjiev.ormnext.core.query.core.AndCondition;
import ru.saidgadjiev.ormnext.core.query.core.condition.Expression;

import java.util.ArrayList;
import java.util.List;

public class Criteria {

    private Expression where = new Expression();

    private AndCondition andCondition = new AndCondition();

    private List<CriterionArgument> args = new ArrayList<>();

    public Criteria() {
        where.add(andCondition);
    }

    public Criteria add(Criterion criterion) {
        andCondition.add(criterion.getCondition());
        args.add(criterion.getArg());

        return this;
    }

    public Criteria or() {
        this.andCondition = new AndCondition();
        where.add(andCondition);

        return this;
    }


    public List<CriterionArgument> getArgs() {
        return args;
    }

    public Expression getWhere() {
        return where;
    }

}
