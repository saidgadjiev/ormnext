package ru.saidgadjiev.ormnext.core.query.core.constraints.attribute;

import ru.saidgadjiev.ormnext.core.query.core.literals.Literal;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * Created by said on 21.01.2018.
 */
public class Default implements AttributeConstraint {

    private String value;

    public Default(String value) {
        this.value = value;
    }

    public String getDefaultValue() {
        return value;
    }


    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
