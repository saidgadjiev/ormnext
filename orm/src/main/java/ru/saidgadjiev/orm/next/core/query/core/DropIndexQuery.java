package ru.saidgadjiev.orm.next.core.query.core;

import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

public class DropIndexQuery implements QueryElement {

    private String name;

    public DropIndexQuery(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }

}
