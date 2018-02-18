package ru.saidgadjiev.orm.next.core.criteria;

import ru.saidgadjiev.orm.next.core.query.core.condition.Condition;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public interface Criterion {

    Condition getCondition();

    Object[] getArgs();
}
