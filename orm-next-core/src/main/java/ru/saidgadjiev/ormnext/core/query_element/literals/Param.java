package ru.saidgadjiev.ormnext.core.query_element.literals;

import ru.saidgadjiev.ormnext.core.query_element.Operand;
import ru.saidgadjiev.ormnext.core.loader.visitor.QueryVisitor;

/**
 * This class use for prepared statement param '?' instead of r_value.
 */
public class Param implements RValue, Operand {

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
