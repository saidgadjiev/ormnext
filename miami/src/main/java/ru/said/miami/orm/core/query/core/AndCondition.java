package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.query.core.condition.Condition;
import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.List;

public class AndCondition implements Condition, QueryElement {

    private List<Condition> conditions = new ArrayList<>();

    public void add(Condition qualification) {
        conditions.add(qualification);
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
