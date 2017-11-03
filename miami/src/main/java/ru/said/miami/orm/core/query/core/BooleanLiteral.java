package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.query.visitor.QueryVisitor;

public class BooleanLiteral implements Operand, RValue {

    private boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }

    @Override
    public void accept(QueryVisitor visitor) {

    }
}
