package ru.saidgadjiev.ormnext.core.query.visitor.element.constraints.attribute;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * Unique attribute constraint.
 *
 * @author said gadjiev
 */
public class UniqueAttributeConstraint implements AttributeConstraint {

    /**
     * Create a new instance.
     */
    public UniqueAttributeConstraint() { }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
