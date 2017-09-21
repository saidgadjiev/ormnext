package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.List;

public class AndCondition implements QueryElement {

    private List<Condition> conditions = new ArrayList<>();

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
