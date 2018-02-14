package ru.saidgadjiev.orm.next.core.criteria;

import ru.saidgadjiev.orm.next.core.query.core.AndCondition;
import ru.saidgadjiev.orm.next.core.query.core.condition.Expression;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Criteria implements QueryElement {

    private Expression expression = new Expression();

    private AndCondition andCondition = new AndCondition();

    private Map<Integer, Object> args = new HashMap<>();

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
        args.put(index.incrementAndGet(), value);
    }

    public Expression prepare() {
        return expression;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        expression.accept(visitor);
    }
}
