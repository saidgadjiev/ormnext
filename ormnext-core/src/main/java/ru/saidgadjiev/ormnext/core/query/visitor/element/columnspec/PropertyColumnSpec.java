package ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * This class represent column in select except in select column list.
 *
 * @author Said Gadjiev
 */
public class PropertyColumnSpec extends ColumnSpec {

    /**
     * True if this element was resolved.
     */
    private boolean resolved;

    /**
     * Create new instance with provided column name.
     * @param name target column name
     */
    public PropertyColumnSpec(String name) {
        super(name);
    }

    /**
     * Create new instance with provided column name and alias.
     * @param name target column name
     * @param alias target column alias
     */
    public PropertyColumnSpec(String name, String alias) {
        super(name, alias);
    }

    /**
     * Set resolved for this element.
     */
    public void setResolved() {
        resolved = true;
    }

    /**
     * Is resolved?
     *
     * @return is resolved?
     */
    public boolean isResolved() {
        return resolved;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            getAlias().accept(visitor);
        }
    }
}
