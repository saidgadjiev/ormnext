package ru.saidgadjiev.ormnext.core.query.visitor.element;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * This class represent limit part sql.
 *
 * @author Said Gadjiev
 */
public class Limit implements QueryElement {

    /**
     * Limit.
     */
    private final int limit;

    /**
     * Create a new instance.
     * @param limit target limit value
     */
    public Limit(int limit) {
        this.limit = limit;
    }

    /**
     * Return current limit.
     * @return current limit
     */
    public int getLimit() {
        return limit;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
