package ru.saidgadjiev.orm.next.core.query.core;

import ru.saidgadjiev.orm.next.core.query.core.condition.Condition;
import ru.saidgadjiev.orm.next.core.query.core.condition.LogicalCondition;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.List;

public class AndCondition implements LogicalCondition, QueryElement {

    private final List<Condition> conditions = new ArrayList<>();

    public void add(Condition qualification) {
        conditions.add(qualification);
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }

}
