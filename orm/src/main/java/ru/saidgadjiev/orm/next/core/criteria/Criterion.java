package ru.saidgadjiev.orm.next.core.criteria;

import ru.saidgadjiev.orm.next.core.query.core.condition.Condition;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class Criterion {

    private Condition condition;

    private Queue<Object> args = new LinkedList<>();

    public Criterion(Condition condition, Collection<Object> args) {
        this.condition = condition;
        this.args.addAll(args);
    }

    public Condition getCondition() {
        return condition;
    }

    public Queue<Object> getArgs() {
        return args;
    }
}
