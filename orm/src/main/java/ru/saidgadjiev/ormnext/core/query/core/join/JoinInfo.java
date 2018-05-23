package ru.saidgadjiev.ormnext.core.query.core.join;

import ru.saidgadjiev.ormnext.core.query.core.common.TableRef;
import ru.saidgadjiev.ormnext.core.query.core.condition.Expression;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * This class represent join info-table name and join expression.
 */
public class JoinInfo implements QueryElement {

    /**
     * Table name.
     * @see TableRef
     */
    private TableRef tableRef;

    /**
     * Join expression.
     * @see Expression
     */
    private Expression expression = new Expression();

    /**
     * Create new instance.
     * @param tableRef target table name
     */
    public JoinInfo(TableRef tableRef) {
        this.tableRef = tableRef;
    }

    /**
     * Return current table name.
     * @return tableRef
     * @see TableRef
     */
    public TableRef getTableRef() {
        return tableRef;
    }

    /**
     * Return current join expression.
     * @return expression
     */
    public Expression getExpression() {
        return expression;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
        expression.accept(visitor);

    }
}
