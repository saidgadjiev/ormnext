package ru.saidgadjiev.orm.next.core.criteria;

import ru.saidgadjiev.orm.next.core.query.core.AndCondition;
import ru.saidgadjiev.orm.next.core.query.core.condition.Expression;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class Criteria implements CriteriaElement {

    private Expression expression = new Expression();

    private AndCondition andCondition = new AndCondition();

    private Queue<Object> args = new LinkedList<>();

    private AtomicInteger index = new AtomicInteger();

    public Criteria() {
        expression.add(andCondition);
    }

    public Criteria add(Criterion criterion) {
        andCondition.add(criterion.getCondition());
        addToArg(criterion.getValue());

        return this;
    }

    public Criteria or() {
        this.andCondition = new AndCondition();
        expression.add(andCondition);

        return this;
    }

    private void addToArg(Object value) {
        args.add(value);
    }

    public Queue<Object> getArgs() {
        return args;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void accept(CriteriaVisitor visitor) {
        expression.accept(visitor);
    }
}
