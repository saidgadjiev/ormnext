package ru.saidgadjiev.ormnext.core.query.visitor.element.constraints.attribute;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * Attribute not null constraint.
 *
 * @author Said Gadjiev
 */
public class NotNullConstraint implements AttributeConstraint {

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
