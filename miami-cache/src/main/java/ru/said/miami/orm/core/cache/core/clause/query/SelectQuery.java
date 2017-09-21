package ru.said.miami.orm.core.cache.core.clause.query;

import ru.said.miami.orm.core.cache.core.dao.visitor.QueryElement;
import ru.said.miami.orm.core.cache.core.dao.visitor.QueryVisitor;

import java.util.List;

/**
 * Created by said on 17.06.17.
 */
public class SelectQuery implements QueryElement {

    private String from;
    private ORDER_BY orderBy;
    private Integer limit;
    private List<WhereValue> values;

    public SelectQuery(String from) {
        this.from = from;
    }

    public void add(WhereValue value) {
        values.add(value);
    }

    public void limit(Integer limit) {
        this.limit = limit;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
    }

    public enum ORDER_BY {
        ASC,
        DESC
    }
}
