package ru.saidgadjiev.orm.next.core.criteria.impl;

import ru.saidgadjiev.orm.next.core.criteria.api.Criterion;
import ru.saidgadjiev.orm.next.core.query.core.AndCondition;
import ru.saidgadjiev.orm.next.core.query.core.condition.Expression;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class Criteria {

    private Expression expression = new Expression();

    private AndCondition andCondition = new AndCondition();

    private Queue<Object> args = new LinkedList<>();

    public Criteria() {
        expression.add(andCondition);
    }

    public Criteria add(Criterion criterion) {
        andCondition.add(criterion.getCondition());
        addToArg(criterion.getArgs());

        return this;
    }

    public Criteria in(Criterion criterion) {
        andCondition.add(criterion.getCondition());
        addToArg(criterion.getArgs());

        return this;
    }

    public Criteria or() {
        this.andCondition = new AndCondition();
        expression.add(andCondition);

        return this;
    }

    private void addToArg(Object[] values) {
        args.addAll(Arrays.asList(values));
    }

    public Queue<Object> getArgs() {
        return args;
    }

    public Expression getExpression() {
        return expression;
    }

}
