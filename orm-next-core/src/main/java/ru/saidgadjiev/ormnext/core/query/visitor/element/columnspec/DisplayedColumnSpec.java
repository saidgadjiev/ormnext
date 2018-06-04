package ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.element.Alias;

/**
 * Base class for select column item.
 *
 * @author said gadjiev
 */
public abstract class DisplayedColumnSpec implements QueryElement {

    /**
     * SelectQuery column alias eg. select name as test
     */
    private Alias alias;

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
