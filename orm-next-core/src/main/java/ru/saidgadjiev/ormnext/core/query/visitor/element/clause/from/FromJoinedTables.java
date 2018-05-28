package ru.saidgadjiev.ormnext.core.query.visitor.element.clause.from;

import ru.saidgadjiev.ormnext.core.query.visitor.element.common.TableRef;
import ru.saidgadjiev.ormnext.core.query.visitor.element.join.JoinExpression;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class represent from clause in select with joins.
 */
public class FromJoinedTables implements FromExpression {

    /**
     * Main table.
     * @see TableRef
     */
    private TableRef tableRef;

    /**
     * Join expressions. It is includes info about join expressions.
     * @see JoinExpression
     */
    private List<JoinExpression> joinExpressions = new ArrayList<>();

    /**
     * Create instance with provided main table.
     * @param tableRef target table
     */
    public FromJoinedTables(TableRef tableRef) {
        this.tableRef = tableRef;
    }

    /**
     * Return current main table ref.
     * @return tableRef
     */
    public TableRef getTableRef() {
        return tableRef;
    }

    /**
     * Method for add new join expression.
     * @param joinExpression target join expression
     */
    public void add(JoinExpression joinExpression) {
        joinExpressions.add(joinExpression);
    }

    /**
     * Return current join expressions.
     * @return joinExpressions
     */
    public List<JoinExpression> getJoinExpression() {
        return joinExpressions;
    }

    /**
     * Add all provided join expressions.
     * @param joinExpressions target join expressions
     */
    public void addAll(Collection<JoinExpression> joinExpressions) {
        this.joinExpressions.addAll(joinExpressions);
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            tableRef.accept(visitor);
            joinExpressions.forEach(joinExpression -> joinExpression.accept(visitor));
        }
    }
}
