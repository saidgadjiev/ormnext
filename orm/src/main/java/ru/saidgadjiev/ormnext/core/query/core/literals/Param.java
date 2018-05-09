package ru.saidgadjiev.ormnext.core.query.core.literals;

import ru.saidgadjiev.ormnext.core.query.core.Operand;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * Created by said on 04.11.17.
 */
public class Param implements RValue, Operand {


    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
