package ru.saidgadjiev.ormnext.core.query.core.clause.from;

import ru.saidgadjiev.ormnext.core.query.core.Select;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

public class FromSubQuery implements FromExpression {

    private Select select;

    public FromSubQuery(Select select) {
        this.select = select;
    }

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
