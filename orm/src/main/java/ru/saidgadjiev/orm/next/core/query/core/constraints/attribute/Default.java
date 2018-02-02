package ru.saidgadjiev.orm.next.core.query.core.constraints.attribute;

import ru.saidgadjiev.orm.next.core.query.core.literals.Literal;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

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
        visitor.start(this);
        visitor.finish(this);
    }
}
