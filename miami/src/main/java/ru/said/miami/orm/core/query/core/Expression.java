package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 09.09.17.
 */
public class Expression implements Condition {

    private List<AndCondition> conditions = new ArrayList<>();

    public void add(AndCondition condition) {
        conditions.add(condition);
    }

    public List<AndCondition> getConditions() {
        return conditions;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        for (AndCondition andCondition: conditions) {
            andCondition.accept(visitor);
        }
        visitor.finish(this);
    }
}
