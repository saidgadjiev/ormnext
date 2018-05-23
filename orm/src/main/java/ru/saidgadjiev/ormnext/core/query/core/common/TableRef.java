package ru.saidgadjiev.ormnext.core.query.core.common;

import ru.saidgadjiev.ormnext.core.query.core.Alias;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * This class represent table name.
 */
public class TableRef implements QueryElement {

    /**
     * Table name.
     */
    private final String tableName;

    /**
     * Table alias.
     * @see Alias
     */
    private Alias alias;

    /**
     * Create new instance with provided table name.
     * @param tableName target table name
     */
    public TableRef(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Provide table alias.
     * @param alias target alias
     * @return this instance for chain
     */
    public TableRef alias(Alias alias) {
        this.alias = alias;

        return this;
    }

    /**
     * Get current table name.
     * @return tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Return current table alias.
     * @return alias
     */
    public Alias getAlias() {
        return alias;
    }

    @Override
    public void accept(QueryVisitor visitor) {
       if (visitor.visit(this)) {
           alias.accept(visitor);
       }
    }
}

