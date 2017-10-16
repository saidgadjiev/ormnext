package ru.said.miami.orm.core.query.core.query_builder;

import ru.said.miami.orm.core.query.core.*;

/**
 * Created by said on 14.10.17.
 */
public class Where {

    private Expression where = new Expression();

    private AndCondition andCondition = null;

    private Condition condition;

    public Where eq(String name, String value) {
        condition = new Equals(new ColumnSpec(name), new StringLiteral(value));

        return this;
    }

    public Where eq(String name, Integer value) {
        condition = new Equals(new ColumnSpec(name), new IntLiteral(value));

        return this;
    }

    public Where and() {
        andCondition.add(condition);
        condition = null;

        return this;
    }

    public Where or() {
        where.add(andCondition);
        andCondition = new AndCondition();

        return this;
    }

    //TODO:Добавить метод prepare QueryBuilder<T>
    Expression getWhere() {
        if (condition != null) {
            andCondition = new AndCondition();
            andCondition.add(condition);
            where.add(andCondition);
        }

        return where;
    }
}
