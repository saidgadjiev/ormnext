package ru.saidgadjiev.ormnext.core.query.core.columnspec;

import ru.saidgadjiev.ormnext.core.query.core.Alias;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;

/**
 * Base class for select column item.
 */
public abstract class DisplayedColumnSpec implements QueryElement {

    /**
     * Select column alias eg. select name as test
     */
    protected Alias alias;

    /**
     * Provide select column alias.
     * @param alias target column alias
     */
    public void setAlias(Alias alias) {
        this.alias = alias;
    }

    /**
     * Return current column alias.
     * @return alias
     */
    public Alias getAlias() {
        return alias;
    }
}
