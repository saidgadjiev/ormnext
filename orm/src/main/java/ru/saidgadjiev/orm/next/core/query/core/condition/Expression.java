package ru.saidgadjiev.orm.next.core.query.core.condition;

import ru.saidgadjiev.orm.next.core.query.core.AndCondition;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

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
        visitor.finish(this);
    }
}
