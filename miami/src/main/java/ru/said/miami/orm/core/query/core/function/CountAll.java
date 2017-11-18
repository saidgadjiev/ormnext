package ru.said.miami.orm.core.query.core.function;

import ru.said.miami.orm.core.query.visitor.QueryVisitor;

public class CountAll implements AgregateFunction {

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
