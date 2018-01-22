package ru.said.orm.next.core.query.core.clause.from;

import ru.said.orm.next.core.query.core.Select;
import ru.said.orm.next.core.query.visitor.QueryVisitor;

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

    }
}
