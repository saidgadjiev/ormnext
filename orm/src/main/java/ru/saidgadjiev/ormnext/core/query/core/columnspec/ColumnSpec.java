package ru.saidgadjiev.ormnext.core.query.core.columnspec;

import ru.saidgadjiev.ormnext.core.query.core.Alias;
import ru.saidgadjiev.ormnext.core.query.core.Operand;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * This class represent column in select except in select column list.
 */
public class ColumnSpec implements Operand {

    /**
     * Column name.
     */
    private String name;

    /**
     * Column alias.
     * @see Alias
     */
    private Alias alias;

    /**
     * Create new instance with provided column name.
     * @param name target column name
     */
    public ColumnSpec(String name) {
        this.name = name;
    }

    /**
     * Provide column alias.
     * @param alias target column alias
     * @return this instance for chain
     */
    public ColumnSpec alias(Alias alias) {
        this.alias = alias;

        return this;
    }

    /**
     * Return current column alias.
     * @return alias
     */
    public Alias getAlias() {
        return alias;
    }

    /**
     * Return current column name.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Provide column name.
     * @param name target column name
     * @return this instance for chain
     */
    public ColumnSpec name(String name) {
        this.name = name;

        return this;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            alias.accept(visitor);
        }
    }

}
