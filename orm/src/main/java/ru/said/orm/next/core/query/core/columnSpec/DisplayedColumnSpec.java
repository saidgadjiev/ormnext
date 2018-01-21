package ru.said.miami.orm.core.query.core.columnSpec;

import ru.said.miami.orm.core.query.core.Alias;
import ru.said.miami.orm.core.query.visitor.QueryElement;

public abstract class DisplayedColumnSpec implements QueryElement {

    protected Alias alias;

    public void setAlias(Alias alias) {
        this.alias = alias;
    }

    public Alias getAlias() {
        return alias;
    }
}
