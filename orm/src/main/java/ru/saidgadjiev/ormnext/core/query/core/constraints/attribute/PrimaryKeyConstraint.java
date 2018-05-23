package ru.saidgadjiev.ormnext.core.query.core.constraints.attribute;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * Attribute primary key constraint.
 */
public class PrimaryKeyConstraint implements AttributeConstraint {

    /**
     * Is generated key.
     */
    private boolean generated;

    /**
     * Create new instance.
     * @param generated true for autogenerated key else false.
     */
    public PrimaryKeyConstraint(boolean generated) {
        this.generated = generated;
    }

    /**
     * Return is generated.
     * @return generated
     */
    public boolean isGenerated() {
        return generated;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
