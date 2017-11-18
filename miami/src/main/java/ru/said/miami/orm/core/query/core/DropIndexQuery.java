package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

public class DropIndexQuery implements QueryElement {

    private String name;

    private DropIndexQuery(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }

    public static DropIndexQuery build(String name) {
        return new DropIndexQuery(name);
    }

}
