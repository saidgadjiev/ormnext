package ru.said.orm.next.core.query.core.literals;

import ru.said.orm.next.core.query.visitor.QueryVisitor;

public class IntLiteral implements Literal<Integer> {

    private Integer value;

    public IntLiteral(Integer value) {
        this.value = value;
    }

    @Override
    public String getOriginal() {
        return String.valueOf(value);
    }

    @Override
    public Integer get() {
        return value;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
