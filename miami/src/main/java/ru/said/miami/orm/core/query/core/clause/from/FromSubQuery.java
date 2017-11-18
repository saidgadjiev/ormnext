package ru.said.miami.orm.core.query.core.clause.from;

import ru.said.miami.orm.core.query.core.Select;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

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
