package ru.said.miami.orm.core.query.core;


import ru.said.miami.orm.core.query.core.sqlQuery.Operand;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

public class ColumnSpec implements Operand {

    private final String name;

    public ColumnSpec(String name) {
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
}
