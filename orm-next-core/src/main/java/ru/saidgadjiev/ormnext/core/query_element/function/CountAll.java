package ru.saidgadjiev.ormnext.core.query_element.function;

import ru.saidgadjiev.ormnext.core.loader.visitor.QueryVisitor;

/**
 * This class COUNT(*).
 */
public class CountAll implements Function {

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
