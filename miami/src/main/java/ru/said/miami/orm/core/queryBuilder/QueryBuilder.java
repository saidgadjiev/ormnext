package ru.said.miami.orm.core.queryBuilder;

import ru.said.miami.orm.core.field.fieldTypes.DBFieldType;
import ru.said.miami.orm.core.query.core.Alias;
import ru.said.miami.orm.core.query.core.Select;
import ru.said.miami.orm.core.query.core.clause.GroupBy;
import ru.said.miami.orm.core.query.core.clause.GroupByItem;
import ru.said.miami.orm.core.query.core.clause.Having;
import ru.said.miami.orm.core.query.core.clause.from.FromJoinedTables;
import ru.said.miami.orm.core.query.core.clause.from.FromSubQuery;
import ru.said.miami.orm.core.query.core.clause.from.FromTable;
import ru.said.miami.orm.core.query.core.clause.select.SelectAll;
import ru.said.miami.orm.core.query.core.clause.select.SelectColumnsList;
import ru.said.miami.orm.core.query.core.columnSpec.*;
import ru.said.miami.orm.core.query.core.common.TableRef;
import ru.said.miami.orm.core.query.core.condition.Expression;
import ru.said.miami.orm.core.query.core.function.CountAll;
import ru.said.miami.orm.core.query.core.join.JoinExpression;
import ru.said.miami.orm.core.query.core.join.LeftJoin;
import ru.said.miami.orm.core.query.visitor.DefaultVisitor;
import ru.said.miami.orm.core.table.TableInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueryBuilder<T> {

    private Expression where;

    private Select selectQuery;

    private TableRef tableRef;
    private TableInfo<T> tableInfo;

    private List<JoinExpression> joinExpressions = new ArrayList<>();

    private Having having;

    private GroupBy groupBy;

    private Select subQuery;

    private Alias alias;

    private SelectColumnsList selectColumnsList;

    private List<DBFieldType> resultFieldTypes;

    public QueryBuilder(TableInfo<T> tableInfo) {
        this.alias = new Alias(tableInfo.getTableName());
        this.tableRef = new TableRef(tableInfo.getTableName());
        this.tableInfo = tableInfo;
        this.selectQuery = new Select();
    }

    public void setAlias(String alias) {
        this.alias.setAlias(alias);

        if (tableRef.getAlias() == null) {
           tableRef.alias(this.alias);
        }
    }

    public String getAlias() {
        return alias.getAlias();
    }

    public FunctionBuilder functionBuilder() {
        return new FunctionBuilder(alias, tableInfo);
    }

    public QueryBuilder<T> having(Having having) {
        this.having = having;

        return this;
    }

    public HavingBuilder havingBuilder() {
        return new HavingBuilder();
    }

    public GroupByItem createGroupByItem(String name) {
        return new GroupByItem(new ColumnSpec(getFieldType(name).getColumnName()).alias(alias));
    }

    public GroupByBuilder groupByBuilder() {
        return new GroupByBuilder();
    }

    public QueryBuilder<T> groupBy(GroupBy groupBy) {
        this.groupBy = groupBy;

        return this;
    }

    public QueryBuilder<T> groupBy(String ... columns) {
        GroupByBuilder groupByBuilder = new GroupByBuilder();

        for (String name: columns) {
            groupByBuilder.add(createGroupByItem(name));
        }
        this.groupBy = groupByBuilder.build();

        return this;
    }

    public ColumnSpec createColumnSpec(String name) {
        return new ColumnSpec(getFieldType(name).getColumnName()).alias(alias);
    }

    public JoinBuilder joinBuilder(QueryBuilder<?> queryBuilder) {
        return new JoinBuilder(alias, queryBuilder.alias, queryBuilder.tableRef, tableInfo, queryBuilder.tableInfo);
    }

    public QueryBuilder<T> leftJoin(LeftJoin leftJoin) {
        joinExpressions.add(leftJoin);

        return this;
    }

    public SelectOperandBuilder selectOperandBuilder() {
        return new SelectOperandBuilder();
    }

    public SelectColumnBuilder selectColumnBuilder() {
        return new SelectColumnBuilder();
    }

    public QueryBuilder<T> selectColumns(DisplayedColumnSpec ... displayedColumnSpecs) {
        if (selectColumnsList == null) {
            selectColumnsList = new SelectColumnsList();
        }
        for (DisplayedColumnSpec displayedColumnSpec: displayedColumnSpecs) {
            selectColumnsList.addColumn(displayedColumnSpec);
        }

        return this;
    }

    public QueryBuilder<T> selectColumns(String ... columns) {
        if (selectColumnsList == null) {
            selectColumnsList = new SelectColumnsList();
            resultFieldTypes = new ArrayList<>();
        }
        for (String name: columns) {
            selectColumnsList.addColumn(new DisplayedColumns(new ColumnSpec(getFieldType(name).getColumnName()).alias(alias)));
            resultFieldTypes.add(getFieldType(name));
        }

        return this;
    }

    public QueryBuilder<T> fromSubQuery(QueryBuilder<?> queryBuilder) {
        queryBuilder.buildSelect();
        subQuery = queryBuilder.getSelect();

        return this;
    }

    public QueryBuilder<T> where(Expression where) {
        this.where = where;

        return this;
    }

    public WhereBuilder whereBuilder() {
        return new WhereBuilder(alias, tableInfo);
    }

    private void buildSelect() {
        if (selectColumnsList != null) {
            selectQuery.setSelectColumnsStrategy(selectColumnsList);
        } else {
            selectQuery.setSelectColumnsStrategy(new SelectAll());
        }
        if (joinExpressions.size() > 0) {
            FromJoinedTables fromJoinedTables = new FromJoinedTables(tableRef);

            fromJoinedTables.getJoinExpression().addAll(joinExpressions);
            selectQuery.setFrom(fromJoinedTables);
        } else if (subQuery != null) {
            selectQuery.setFrom(new FromSubQuery(subQuery));
        } else {
            selectQuery.setFrom(new FromTable(tableRef));
        }
        if (where != null) {
            selectQuery.setWhere(where);
        }
        if (groupBy != null) {
            selectQuery.setGroupBy(groupBy);
        }
        if (having != null) {
            selectQuery.setHaving(having);
        }
    }

    Select getSelect() {
        return selectQuery;
    }

    public PreparedQuery prepare() throws SQLException {
        buildSelect();
        DefaultVisitor defaultVisitor = new DefaultVisitor();

        selectQuery.accept(defaultVisitor);

        return new PreparedQuery(defaultVisitor.getQuery(), resultFieldTypes);
    }

    private DBFieldType getFieldType(String fieldName) {
        return tableInfo.getFieldTypeByFieldName(fieldName)
                .orElseThrow(() ->  new IllegalArgumentException("Field[" + fieldName + "] does,t found"));
    }
}
