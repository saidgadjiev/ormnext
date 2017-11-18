package ru.said.miami.orm.core.queryBuilder;

import ru.said.miami.orm.core.query.core.columnSpec.ColumnSpec;
import ru.said.miami.orm.core.query.core.condition.Expression;
import ru.said.miami.orm.core.query.core.function.AgregateFunction;
import ru.said.miami.orm.core.query.core.function.CountExpression;
import ru.said.miami.orm.core.query.core.function.SUM;

public class FunctionWhere<T> extends BaseWhere {

    private QueryBuilder<T> qb;

    FunctionWhere(QueryBuilder<T> qb, Expression expression) {
        super(expression);
        this.qb = qb;
    }

    public FunctionWhere column(String name) {
        ColumnSpec columnSpec = new ColumnSpec(name).alias(qb.accessAlias());

        super.operandCondition(columnSpec);

        return this;
    }

    public AgregateFunction sum() {
        checkCurrentCondition();

        return new SUM(where);
    }

    public AgregateFunction count() {
        checkCurrentCondition();

        return new CountExpression(where);
    }
}
