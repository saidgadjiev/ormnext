package ru.saidgadjiev.ormnext.core.query.visitor.element;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.condition.Expression;

/**
 * This class represent DELETE sql query.
 *
 * @author said gadjiev
 */
public class DeleteQuery implements SqlStatement {

    /**
     * Where clause.
     * @see Expression
     */
    private Expression where = new Expression();

    /**
     * Table name.
     */
    private final String tableName;

    /**
     * Create a new instance.
     * @param tableName target table name
     */
    public DeleteQuery(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Return current where clause.
     * @return current where clause
     */
    public Expression getWhere() {
        return where;
    }

    /**
     * Provide where clause.
     * @param where target where expression
     * @see Expression
     */
    public void setWhere(Expression where) {
        this.where = where;
    }

    /**
     * Return current table name.
     * @return current table name
     */
    public String getTableName() {
        return tableName;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            where.accept(visitor);
        }
    }
}
