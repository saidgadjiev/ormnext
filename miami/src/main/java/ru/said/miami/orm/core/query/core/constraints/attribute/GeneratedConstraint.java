package ru.said.miami.orm.core.query.core.constraints.attribute;

import ru.said.miami.orm.core.query.visitor.QueryVisitor;

public class GeneratedConstraint implements AttributeConstraint {

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
