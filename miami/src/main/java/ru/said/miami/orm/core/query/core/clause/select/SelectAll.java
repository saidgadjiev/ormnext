package ru.said.miami.orm.core.query.core.clause.select;

import ru.said.miami.orm.core.query.visitor.QueryVisitor;

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
