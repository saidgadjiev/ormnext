package ru.saidgadjiev.orm.next.core.criteria.impl;

import ru.saidgadjiev.orm.next.core.criteria.api.Criterion;
import ru.saidgadjiev.orm.next.core.query.core.AndCondition;
import ru.saidgadjiev.orm.next.core.query.core.condition.Expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Criteria {

    private Expression where = new Expression();

    private AndCondition andCondition = new AndCondition();

    private List<Object> args = new ArrayList<>();

    public Criteria() {
        where.add(andCondition);
    }

    public Criteria add(Criterion criterion) {
        andCondition.add(criterion.getCondition());
        addToArg(criterion.getArgs());

        return this;
    }

    public Criteria or() {
        this.andCondition = new AndCondition();
        where.add(andCondition);

        return this;
    }

    private void addToArg(Object[] values) {
        args.addAll(Arrays.asList(values));
    }

    public List<Object> getArgs() {
        return args;
    }

    public Expression getWhere() {
        return where;
    }

}
