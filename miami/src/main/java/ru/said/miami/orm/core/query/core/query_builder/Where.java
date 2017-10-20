package ru.said.miami.orm.core.query.core.query_builder;

import ru.said.miami.orm.core.query.core.*;

/**
 * Created by said on 14.10.17.
 */
public class Where<T> implements IPreparedQuery<T> {

    private Expression where = new Expression();

    private AndCondition andCondition = null;

    private Condition condition;

    private IPreparedQuery<T> preparedQuery;

    Where(IPreparedQuery<T> preparedQuery) {
        this.preparedQuery = preparedQuery;
    }

    public Where<T> eq(String name, String value) {
        condition = new Equals(new ColumnSpec(name), new StringLiteral(value));

        return this;
    }

    public Where<T> eq(String name, Integer value) {
        condition = new Equals(new ColumnSpec(name), new IntLiteral(value));

        return this;
    }

    public Where<T> and() {
        andCondition.add(condition);
        condition = null;

        return this;
    }

    public Where<T> or() {
        where.add(andCondition);
        andCondition = new AndCondition();

        return this;
    }

    Expression getWhere() {
        if (condition != null) {
            andCondition = new AndCondition();
            andCondition.add(condition);
            where.add(andCondition);
        }

        return where;
    }

    public Query<T> prepare() {
        return preparedQuery.prepare();
    }
}
