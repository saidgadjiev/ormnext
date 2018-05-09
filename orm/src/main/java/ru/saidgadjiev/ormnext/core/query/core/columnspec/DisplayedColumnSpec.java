package ru.saidgadjiev.ormnext.core.query.core.columnspec;

import ru.saidgadjiev.ormnext.core.query.core.Alias;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;

public abstract class DisplayedColumnSpec implements QueryElement {

    protected Alias alias;

    public void setAlias(Alias alias) {
        this.alias = alias;
    }

    public Alias getAlias() {
        return alias;
    }
}
