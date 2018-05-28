package ru.saidgadjiev.ormnext.core.query.visitor.element.common;

import ru.saidgadjiev.ormnext.core.query.visitor.element.Alias;
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
     * Create new instance with provided table name and alias.
     * @param tableName target table name
     * @param alias target table alias
     */
    public TableRef(String tableName, String alias) {
        this.tableName = tableName;
        this.alias = new Alias(alias);
    }

    /**
     * Provide table alias.
     * @param alias target alias
     * @return this instance for chain
     */
    public TableRef alias(String alias) {
        this.alias = new Alias(alias);

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

