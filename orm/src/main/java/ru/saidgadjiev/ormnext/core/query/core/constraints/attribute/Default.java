package ru.saidgadjiev.ormnext.core.query.core.constraints.attribute;

import ru.saidgadjiev.ormnext.core.query.core.literals.Literal;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * Created by said on 21.01.2018.
 */
public class Default<T> implements AttributeConstraint {

    private Literal<T> literal;

    public Default(Literal<T> literal) {
        this.literal = literal;
    }

    public Literal<T> getLiteral() {
        return literal;
    }


    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            literal.accept(visitor);
        }
    }
}
