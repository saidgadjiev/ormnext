package ru.saidgadjiev.ormnext.core.query.visitor.element.condition;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.AndCondition;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains restriction groups separated by 'and' or 'or'.
 *
 * @author Said Gadjiev
 */
public class Expression implements Condition {

    /**
     * Restriction groups list.
     * @see AndCondition
     */
    private final List<AndCondition> conditions = new ArrayList<>();

    /**
     * Add new restriction group.
     * @param condition target restriction group
     */
    public void add(AndCondition condition) {
        conditions.add(condition);
    }

    /**
     * Return all restriction groups.
     * @return conditions
     */
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
