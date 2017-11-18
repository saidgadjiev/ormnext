package ru.said.miami.orm.core.queryBuilder;

import ru.said.miami.orm.core.field.DataPersisterManager;
import ru.said.miami.orm.core.query.core.Operand;
import ru.said.miami.orm.core.query.core.columnSpec.ColumnSpec;
import ru.said.miami.orm.core.query.core.condition.*;

import java.sql.SQLException;

public class JoinWhere<T> extends BaseWhere {

    private final QueryBuilder<T> sourceQB;

    private final QueryBuilder<?> joinedQB;

    public JoinWhere(QueryBuilder<T> sourceQB, QueryBuilder<?> joinedQB, Expression where) {
        super(where);
        this.sourceQB = sourceQB;
        this.joinedQB = joinedQB;
    }

    public JoinWhere<T> eq(String name1, String name2) {
        super.eq(createColumnSpec(sourceQB, name1), createColumnSpec(joinedQB, name2));

        return this;
    }

    public JoinWhere<T> eq(String name, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.eq(createColumnSpec(sourceQB, name), operand);

        return this;
    }

    public JoinWhere<T> gt(String name, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.gt(createColumnSpec(sourceQB, name), operand);

        return this;
    }

    public JoinWhere<T> gt(String name1, String name2) {
        super.gt(createColumnSpec(sourceQB, name1), createColumnSpec(joinedQB, name2));

        return this;
    }

    public JoinWhere<T> ge(String name, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.ge(createColumnSpec(sourceQB, name), operand);

        return this;
    }

    public JoinWhere<T> ge(String name1, String name2) {
        super.ge(createColumnSpec(sourceQB, name1), createColumnSpec(joinedQB, name2));

        return this;
    }

    public JoinWhere<T> lt(String name, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.lt(createColumnSpec(sourceQB, name), operand);

        return this;
    }

    public JoinWhere<T> lt(String name1, String name2) {
        super.lt(createColumnSpec(sourceQB, name1), createColumnSpec(joinedQB, name2));

        return this;
    }

    public JoinWhere<T> le(String name, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.le(createColumnSpec(sourceQB, name), operand);

        return this;
    }

    public JoinWhere<T> le(String name1, String name2) {
        super.le(createColumnSpec(sourceQB, name1), createColumnSpec(joinedQB, name2));

        return this;
    }

    public JoinWhere<T> and() {
        super.andClause();

        return this;
    }

    public JoinWhere<T> or() {
        super.orClause();

        return this;
    }

    public QueryBuilder<T> build() throws SQLException {
        checkCurrentCondition();

        return sourceQB;
    }

    private ColumnSpec createColumnSpec(QueryBuilder<?> qb, String name) {
        ColumnSpec columnSpec = new ColumnSpec(name).alias(qb.accessAlias());

        return columnSpec;
    }
}
