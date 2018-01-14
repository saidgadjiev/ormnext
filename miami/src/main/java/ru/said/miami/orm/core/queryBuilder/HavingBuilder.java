package ru.said.miami.orm.core.queryBuilder;

import ru.said.miami.orm.core.field.DataPersisterManager;
import ru.said.miami.orm.core.query.core.Operand;
import ru.said.miami.orm.core.query.core.clause.Having;
import ru.said.miami.orm.core.query.core.function.Function;
import ru.said.miami.orm.core.query.core.literals.Param;

public class HavingBuilder extends AbstractWhereBuilder {

    HavingBuilder() {
    }

    public HavingBuilder eq(Operand operand, Object value) {
        Operand valueOperand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.eq(valueOperand, operand);

        return this;
    }

    public HavingBuilder eq(Operand operand) {
        super.eq(operand, new Param());

        return this;
    }

    public HavingBuilder gt(Operand function, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.gt(function, operand);

        return this;
    }

    public HavingBuilder gt(Operand function) {
        super.gt(function, new Param());

        return this;
    }

    public HavingBuilder ge(Operand function, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.ge(function, operand);

        return this;
    }

    public HavingBuilder ge(Operand function) {
        super.ge(function, new Param());

        return this;
    }

    public HavingBuilder lt(Operand function, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.lt(function, operand);

        return this;
    }

    public HavingBuilder lt(Operand function) {
        super.lt(function, new Param());

        return this;
    }

    public HavingBuilder and() {
        super.andClause();

        return this;
    }

    public HavingBuilder or() {
        super.orClause();

        return this;
    }

    public Having build() {
        checkCurrentCondition();

        return new Having(expression);
    }
}
