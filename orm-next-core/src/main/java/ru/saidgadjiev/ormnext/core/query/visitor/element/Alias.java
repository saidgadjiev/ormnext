package ru.saidgadjiev.ormnext.core.query.visitor.element;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * This class represent alias eg. 'test' as 'test1'.
 *
 * @author said gadjiev
 */
public class Alias implements Operand {

    /**
     * Alias.
     */
    private final String alias;

    /**
     * Create a new instance.
     * @param alias target alias
     */
    public Alias(String alias) {
        this.alias = alias;
    }

    /**
     * Return current alias.
     * @return current alias
     */
    public String getAlias() {
        return alias;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
