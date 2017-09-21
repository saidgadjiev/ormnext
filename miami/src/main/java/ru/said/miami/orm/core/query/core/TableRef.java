package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

/**
 * Created by said on 09.09.17.
 */
public class TableRef implements QueryElement {

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
