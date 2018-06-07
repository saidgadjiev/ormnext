package ru.saidgadjiev.ormnext.core.query.visitor.element.clause.select;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * This implementation represent select all(count star) in select.
 *
 * @author Said Gadjiev
 */
public class SelectAll implements SelectColumnsStrategy {

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
