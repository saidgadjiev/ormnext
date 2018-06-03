package ru.saidgadjiev.ormnext.core.query.visitor.element.clause.from;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.SelectQuery;

/**
 * This class represent from clause with sub query.
 */
public class FromSubQuery implements FromExpression {

    /**
     * Sub selectQuery.
     */
    private SelectQuery selectQuery;

    /**
     * Create new instance with provided sub selectQuery.
     * @param selectQuery target sub selectQuery
     */
    public FromSubQuery(SelectQuery selectQuery) {
        this.selectQuery = selectQuery;
    }

    /**
     * Get current sub selectQuery.
     * @return selectQuery
     */
    public SelectQuery getSelectQuery() {
        return selectQuery;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            selectQuery.accept(visitor);
        }
    }
}
