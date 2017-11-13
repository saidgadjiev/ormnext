package ru.said.miami.orm.core.query.core.sqlQuery;

import ru.said.miami.orm.core.query.visitor.DefaultVisitor;
import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

public class DropIndexQuery implements ISQLQuery {

    private String name;
    private QueryVisitor visitor;

    private DropIndexQuery(String name, QueryVisitor visitor) {
        this.name = name;
        this.visitor = visitor;
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
        return new DropIndexQuery(name, new DefaultVisitor());
    }

}
