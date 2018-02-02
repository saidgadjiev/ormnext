package ru.saidgadjiev.orm.next.core.query.core.column_spec;

import ru.saidgadjiev.orm.next.core.query.core.Alias;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;

public abstract class DisplayedColumnSpec implements QueryElement {

    protected Alias alias;

    public void setAlias(Alias alias) {
        this.alias = alias;
    }

    public Alias getAlias() {
        return alias;
    }
}
