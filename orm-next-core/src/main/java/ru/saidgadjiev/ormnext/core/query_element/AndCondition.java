package ru.saidgadjiev.ormnext.core.query_element;

import ru.saidgadjiev.ormnext.core.query_element.condition.Condition;
import ru.saidgadjiev.ormnext.core.loader.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.loader.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represent conditions group eg. 'a' = 'test' and b = 2 or c = 3.
 * This example has two conditions group: 1) 'a' = 'test'; b = 2, 2) c = 3.
 */
public class AndCondition implements Condition, QueryElement {

    /**
     * Current conditions list.
     * @see Condition
     */
    private final List<Condition> conditions = new ArrayList<>();

    /**
     * Add new condition.
     * @param qualification target condition
     */
    public void add(Condition qualification) {
        conditions.add(qualification);
    }

    /**
     * Return current conditions.
     * @return current conditions
     */
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
