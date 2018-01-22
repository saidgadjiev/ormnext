package ru.said.orm.next.core.query.core.literals;

import ru.said.orm.next.core.query.core.Operand;
import ru.said.orm.next.core.query.visitor.QueryVisitor;

/**
 * Created by said on 04.11.17.
 */
public class Param implements Operand, RValue {


    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
