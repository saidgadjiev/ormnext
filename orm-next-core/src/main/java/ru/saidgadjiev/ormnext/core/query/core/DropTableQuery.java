package ru.saidgadjiev.ormnext.core.query.core;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * This class represent DROP TABLE sql query.
 */
public class DropTableQuery implements QueryElement {

    /**
     * Table name.
     */
    private final String tableName;

    /**
     * True if need append if exist.
     */
    private final boolean ifExist;

    /**
     * Create new instance.
     * @param tableName target table name
     * @param ifExist true if need append if exist
     */
    public DropTableQuery(String tableName, boolean ifExist) {
        this.tableName = tableName;
        this.ifExist = ifExist;
    }

    /**
     * Return current table name.
     * @return current table name
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Return is need append if exist.
     * @return is need append if exist
     */
    public boolean isIfExist() {
        return ifExist;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);

    }
}
