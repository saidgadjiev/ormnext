package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.query.visitor.QueryVisitor;

public class IntLiteral implements Operand, RValue {

    private Integer value;

    public IntLiteral(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
