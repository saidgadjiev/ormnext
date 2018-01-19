package ru.said.orm.next.core.query.core.clause.select;

import ru.said.orm.next.core.query.visitor.QueryVisitor;
import ru.said.orm.next.core.query.visitor.QueryVisitor;

/**
 * Created by said on 04.11.17.
 */
public class SelectAll implements SelectColumnsStrategy {

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
