package ru.saidgadjiev.orm.next.core.query.core.constraints.attribute;

import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

public class PrimaryKeyConstraint implements AttributeConstraint {

    private boolean generated;

    public PrimaryKeyConstraint(boolean generated) {
        this.generated = generated;
    }

    public boolean isGenerated() {
        return generated;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
