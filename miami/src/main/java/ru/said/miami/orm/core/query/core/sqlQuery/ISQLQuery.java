package ru.said.miami.orm.core.query.core.sqlQuery;

import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

/**
 * Created by said on 12.11.17.
 */
public interface ISQLQuery extends QueryElement {

    default String getQuery(QueryVisitor visitor) {
        accept(visitor);

        return visitor.getQuery();
    }

}
