package ru.said.orm.next.core.query.core.constraints.attribute;

import ru.said.orm.next.core.query.visitor.QueryVisitor;

public class NotNullConstraint implements AttributeConstraint {

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
