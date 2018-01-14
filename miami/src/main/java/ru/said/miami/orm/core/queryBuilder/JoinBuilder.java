package ru.said.miami.orm.core.queryBuilder;

import ru.said.miami.orm.core.field.DataPersisterManager;
import ru.said.miami.orm.core.query.core.Alias;
import ru.said.miami.orm.core.query.core.Operand;
import ru.said.miami.orm.core.query.core.columnSpec.ColumnSpec;
import ru.said.miami.orm.core.query.core.common.TableRef;
import ru.said.miami.orm.core.query.core.join.LeftJoin;
import ru.said.miami.orm.core.table.TableInfo;

import java.sql.SQLException;

public class JoinBuilder extends AbstractWhereBuilder {

    private final Alias sourceTableAlias;

    private final Alias joinedTableAlias;

    private final TableRef joinedTableRef;

    private final TableInfo<?> sourceTBInfo;

    private final TableInfo<?> joinedTBInfo;

    JoinBuilder(Alias sourceTableAlias, Alias joinedTableAlias, TableRef joinedTableRef, TableInfo<?> sourceTBInfo, TableInfo<?> joinedTBInfo) {
        this.sourceTableAlias = sourceTableAlias;
        this.joinedTableAlias = joinedTableAlias;
        this.joinedTableRef = joinedTableRef;
        this.sourceTBInfo = sourceTBInfo;
        this.joinedTBInfo = joinedTBInfo;
    }

    public JoinBuilder eq(String name1, String name2) {
        super.eq(createColumnSpec(sourceTableAlias, getColumnName(sourceTBInfo, name1)), createColumnSpec(joinedTableAlias, getColumnName(joinedTBInfo, name2)));

        return this;
    }

    public JoinBuilder eq(String name, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.eq(createColumnSpec(sourceTableAlias, getColumnName(sourceTBInfo, name)), operand);

        return this;
    }

    public JoinBuilder gt(String name, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.gt(createColumnSpec(sourceTableAlias, getColumnName(sourceTBInfo, name)), operand);

        return this;
    }

    public JoinBuilder gt(String name1, String name2) {
        super.gt(createColumnSpec(sourceTableAlias, getColumnName(sourceTBInfo, name1)), createColumnSpec(joinedTableAlias, getColumnName(joinedTBInfo, name2)));

        return this;
    }

    public JoinBuilder ge(String name, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.ge(createColumnSpec(sourceTableAlias, getColumnName(sourceTBInfo, name)), operand);

        return this;
    }

    public JoinBuilder ge(String name1, String name2) {
        super.ge(createColumnSpec(sourceTableAlias, getColumnName(joinedTBInfo, name1)), createColumnSpec(joinedTableAlias, getColumnName(joinedTBInfo, name2)));

        return this;
    }

    public JoinBuilder lt(String name, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.lt(createColumnSpec(sourceTableAlias, getColumnName(sourceTBInfo, name)), operand);

        return this;
    }

    public JoinBuilder lt(String name1, String name2) {
        super.lt(createColumnSpec(sourceTableAlias, getColumnName(joinedTBInfo, name1)), createColumnSpec(joinedTableAlias, getColumnName(joinedTBInfo, name2)));

        return this;
    }

    public JoinBuilder le(String name, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        super.le(createColumnSpec(sourceTableAlias, getColumnName(sourceTBInfo, name)), operand);

        return this;
    }

    public JoinBuilder le(String name1, String name2) {
        super.le(createColumnSpec(sourceTableAlias, getColumnName(sourceTBInfo, name1)), createColumnSpec(joinedTableAlias, getColumnName(joinedTBInfo, name2)));

        return this;
    }

    public JoinBuilder and() {
        super.andClause();

        return this;
    }

    public JoinBuilder or() {
        super.orClause();

        return this;
    }

    public LeftJoin leftJoin() throws SQLException {
        checkCurrentCondition();

        return new LeftJoin(joinedTableRef, expression);
    }

    private ColumnSpec createColumnSpec(Alias alias, String name) {
        return new ColumnSpec(name).alias(alias);
    }

    private String getColumnName(TableInfo<?> tableInfo, String fieldName) {
        return tableInfo.getDBFieldTypeByFieldName(fieldName)
                .orElseThrow(() ->  new IllegalArgumentException("Field[" + fieldName + "] does,t found"))
                .getColumnName();
    }
}
