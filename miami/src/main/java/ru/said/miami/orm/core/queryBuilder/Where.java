package ru.said.miami.orm.core.queryBuilder;

import ru.said.miami.orm.core.field.DataPersisterManager;
import ru.said.miami.orm.core.query.core.Operand;
import ru.said.miami.orm.core.query.core.columnSpec.ColumnSpec;
import ru.said.miami.orm.core.query.core.condition.Expression;
import ru.said.miami.orm.core.query.core.literals.Param;
import ru.said.miami.orm.core.query.core.literals.StringLiteral;

import java.sql.SQLException;

/**
 * Created by said on 14.10.17.
 */
public class Where<T> extends BaseWhere {

    private final QueryBuilder<T> preparedQuery;

    public Where(QueryBuilder<T> preparedQuery, Expression where) {
        super(where);
        this.preparedQuery = preparedQuery;
    }

    public Where<T> eq(String name) {
        super.eq(createColumnSpec(name), new Param());

        return this;
    }

    public Where<T> eq(String name, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.eq(createColumnSpec(name), operand);

        return this;
    }

    public Where<T> gt(String name, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.gt(createColumnSpec(name), operand);

        return this;
    }

    public Where<T> gt(String name) {
        super.gt(createColumnSpec(name), new Param());

        return this;
    }

    public Where<T> ge(String name, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.ge(createColumnSpec(name), operand);

        return this;
    }

    public Where<T> ge(String name) {
        super.ge(createColumnSpec(name), new Param());

        return this;
    }

    public Where<T> lt(String name, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.lt(createColumnSpec(name), operand);

        return this;
    }

    public Where<T> lt(String name) {
        super.lt(createColumnSpec(name), new Param());

        return this;
    }

    public Where<T> le(String name, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.le(createColumnSpec(name), operand);
        return this;
    }

    public Where<T> le(String name) {
        super.le(createColumnSpec(name), new Param());

        return this;
    }

    public Where<T> in(String name, QueryBuilder<T> queryBuilder) {
        super.in(queryBuilder.getSelect(), new StringLiteral(name));

        return this;
    }

    public Where<T> notIn(String name, QueryBuilder<T> queryBuilder) {
        super.notIn(queryBuilder.getSelect(), new StringLiteral(name));

        return this;
    }

    public Where<T> exists(String name, QueryBuilder<T> queryBuilder) {
        super.exists(queryBuilder.getSelect(), new StringLiteral(name));

        return this;
    }

    public Where<T> and() {
        super.andClause();

        return this;
    }

    public Where<T> or() {
        super.orClause();

        return this;
    }

    public QueryBuilder<T> build() throws SQLException {
        super.checkCurrentCondition();

        return preparedQuery;
    }

    private ColumnSpec createColumnSpec(String name) {
        return new ColumnSpec(name).alias(preparedQuery.accessAlias());
    }
}
