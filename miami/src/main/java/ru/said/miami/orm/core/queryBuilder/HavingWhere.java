package ru.said.miami.orm.core.queryBuilder;

import ru.said.miami.orm.core.field.DataPersisterManager;
import ru.said.miami.orm.core.query.core.Operand;
import ru.said.miami.orm.core.query.core.condition.Expression;
import ru.said.miami.orm.core.query.core.function.AgregateFunction;
import ru.said.miami.orm.core.query.core.literals.Param;

public class HavingWhere<T> extends BaseWhere {

    private final QueryBuilder<T> qb;

    protected HavingWhere(QueryBuilder<T> qb, Expression where) {
        super(where);
        this.qb = qb;
    }

    public HavingWhere<T> eq(AgregateFunction function, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.eq(function, operand);

        return this;
    }

    public HavingWhere<T> eq(AgregateFunction function) {
        super.eq(function, new Param());

        return this;
    }

    public HavingWhere<T> gt(AgregateFunction function, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.gt(function, operand);

        return this;
    }

    public HavingWhere<T> gt(AgregateFunction function) {
        super.gt(function, new Param());

        return this;
    }

    public HavingWhere<T> ge(AgregateFunction function, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.ge(function, operand);

        return this;
    }

    public HavingWhere<T> ge(AgregateFunction function) {
        super.ge(function, new Param());

        return this;
    }

    public HavingWhere<T> lt(AgregateFunction function, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.lt(function, operand);

        return this;
    }

    public HavingWhere<T> lt(AgregateFunction function) {
        super.lt(function, new Param());

        return this;
    }

    public HavingWhere<T> and() {
        super.andClause();

        return this;
    }

    public HavingWhere<T> or() {
        super.orClause();

        return this;
    }

    public QueryBuilder<T> build() {
        checkCurrentCondition();

        return qb;
    }
}
