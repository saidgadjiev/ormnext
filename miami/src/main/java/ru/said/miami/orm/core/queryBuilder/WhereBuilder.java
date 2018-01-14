package ru.said.miami.orm.core.queryBuilder;

import ru.said.miami.orm.core.field.DataPersisterManager;
import ru.said.miami.orm.core.query.core.Alias;
import ru.said.miami.orm.core.query.core.Operand;
import ru.said.miami.orm.core.query.core.columnSpec.ColumnSpec;
import ru.said.miami.orm.core.query.core.condition.Expression;
import ru.said.miami.orm.core.query.core.literals.Param;
import ru.said.miami.orm.core.query.core.literals.StringLiteral;
import ru.said.miami.orm.core.table.TableInfo;

import java.sql.SQLException;

/**
 * Created by said on 14.10.17.
 */
public class WhereBuilder extends AbstractWhereBuilder {

    private Alias alias;

    private TableInfo<?> tableInfo;

    WhereBuilder(Alias alias, TableInfo<?> tableInfo) {
        this.alias = alias;
        this.tableInfo = tableInfo;
    }

    public WhereBuilder eq(String name) {
        super.eq(createColumnSpec(name), new Param());

        return this;
    }

    public WhereBuilder eq(String name, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.eq(createColumnSpec(name), operand);

        return this;
    }

    public WhereBuilder gt(String name, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.gt(createColumnSpec(name), operand);

        return this;
    }

    public WhereBuilder gt(String name) {
        super.gt(createColumnSpec(name), new Param());

        return this;
    }

    public WhereBuilder ge(String name, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.ge(createColumnSpec(name), operand);

        return this;
    }

    public WhereBuilder ge(String name) {
        super.ge(createColumnSpec(name), new Param());

        return this;
    }

    public WhereBuilder lt(String name, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.lt(createColumnSpec(name), operand);

        return this;
    }

    public WhereBuilder lt(String name) {
        super.lt(createColumnSpec(name), new Param());

        return this;
    }

    public WhereBuilder le(String name, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.le(createColumnSpec(name), operand);
        return this;
    }

    public WhereBuilder le(String name) {
        super.le(createColumnSpec(name), new Param());

        return this;
    }

    public WhereBuilder in(String name, QueryBuilder<?> queryBuilder) {
        super.in(queryBuilder.getSelect(), new StringLiteral(name));

        return this;
    }

    public WhereBuilder notIn(String name, QueryBuilder<?> queryBuilder) {
        super.notIn(queryBuilder.getSelect(), new StringLiteral(name));

        return this;
    }

    public WhereBuilder exists(String name, QueryBuilder<?> queryBuilder) {
        super.exists(queryBuilder.getSelect(), new StringLiteral(name));

        return this;
    }

    public WhereBuilder and() {
        super.andClause();

        return this;
    }

    public WhereBuilder or() {
        super.orClause();

        return this;
    }

    public Expression build() throws SQLException {
        checkCurrentCondition();

        return expression;
    }

    private ColumnSpec createColumnSpec(String name) {
        return new ColumnSpec(getColumnName(name)).alias(alias);
    }

    private String getColumnName(String fieldName) {
        return tableInfo.getFieldTypeByFieldName(fieldName)
                .orElseThrow(() ->  new IllegalArgumentException("Field[" + fieldName + "] does,t found"))
                .getColumnName();
    }
}
