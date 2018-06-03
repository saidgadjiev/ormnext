package ru.saidgadjiev.ormnext.core.query.visitor.element.constraints.attribute;

import ru.saidgadjiev.ormnext.core.field.fieldtype.UniqueColumns;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.constraints.table.TableConstraint;

import java.util.List;

/**
 * Unique attribute constraint.
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
