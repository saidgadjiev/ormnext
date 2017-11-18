package ru.said.miami.orm.core.queryBuilder;

import ru.said.miami.orm.core.field.DataPersisterManager;
import ru.said.miami.orm.core.query.core.Operand;
import ru.said.miami.orm.core.query.core.function.AgregateFunction;

@SuppressWarnings("PMD")
public class SelectColumnsBuilder<T>  {

    private final QueryBuilder<T> qb;

    private Operand operand;

    public SelectColumnsBuilder(QueryBuilder<T> qb, Operand operand) {
        this.qb = qb;
        this.operand = operand;
    }

    public SelectColumnsBuilder<T> function(AgregateFunction function) {
        this.operand = function;

        return this;
    }

    public SelectColumnsBuilder<T> value(Object value) {
        this.operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        return this;
    }

    public QueryBuilder<T> build() {
        return qb;
    }
}
