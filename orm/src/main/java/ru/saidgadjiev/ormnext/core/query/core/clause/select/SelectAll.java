package ru.saidgadjiev.ormnext.core.query.core.clause.select;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * This implementation represent select all(count star) in select.
 */
public class SelectAll implements SelectColumnsStrategy {

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
