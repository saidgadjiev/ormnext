package ru.saidgadjiev.ormnext.core.query.core.condition;

import ru.saidgadjiev.ormnext.core.query.core.AndCondition;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 09.09.17.
 */
public class Expression implements Condition {

    private final List<AndCondition> conditions = new ArrayList<>();

    public void add(AndCondition condition) {
        conditions.add(condition);
    }

    public List<AndCondition> getConditions() {
        return conditions;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            for (AndCondition condition: conditions) {
                condition.accept(visitor);
            }
        }
    }
}
