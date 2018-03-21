package ru.saidgadjiev.orm.next.core.query.core;

import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

/**
 * Created by said on 17.03.2018.
 */
public class Offset implements QueryElement {

    private int offset;

    public Offset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
