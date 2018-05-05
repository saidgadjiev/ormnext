package ru.saidgadjiev.ormnext.core.query.core;

import ru.saidgadjiev.ormnext.core.query.core.condition.Condition;
import ru.saidgadjiev.ormnext.core.query.core.condition.LogicalCondition;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

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
        if (visitor.visit(this)) {
            for (Condition condition: conditions) {
                condition.accept(visitor);
            }
        }
    }

}
