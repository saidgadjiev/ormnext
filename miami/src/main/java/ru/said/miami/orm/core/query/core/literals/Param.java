package ru.said.miami.orm.core.query.core.literals;

import ru.said.miami.orm.core.query.core.sqlQuery.Operand;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

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
