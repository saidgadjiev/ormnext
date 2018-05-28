package ru.saidgadjiev.ormnext.core.query.visitor.element.clause.from;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.Select;

/**
 * This class represent from clause with sub query.
 */
public class FromSubQuery implements FromExpression {

    /**
     * Sub select.
     */
    private Select select;

    /**
     * Create new instance with provided sub select.
     * @param select target sub select
     */
    public FromSubQuery(Select select) {
        this.select = select;
    }

    /**
     * Get current sub select.
     * @return select
     */
    public Select getSelect() {
        return select;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            select.accept(visitor);
        }
    }
}
