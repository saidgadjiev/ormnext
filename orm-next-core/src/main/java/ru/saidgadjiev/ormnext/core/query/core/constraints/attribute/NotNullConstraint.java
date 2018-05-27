package ru.saidgadjiev.ormnext.core.query.core.constraints.attribute;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * Attribute not null constraint.
 */
public class NotNullConstraint implements AttributeConstraint {

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
