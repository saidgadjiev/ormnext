package ru.saidgadjiev.ormnext.core.query.core.function;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * This class COUNT(*).
 */
public class CountAll implements Function {

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
