package ru.saidgadjiev.orm.next.core.query.core.function;

import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

public class CountAll implements Function {

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
