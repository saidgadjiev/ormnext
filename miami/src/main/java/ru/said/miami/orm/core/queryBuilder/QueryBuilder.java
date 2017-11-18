package ru.said.miami.orm.core.queryBuilder;

import ru.said.miami.orm.core.query.core.Alias;
import ru.said.miami.orm.core.query.core.Select;
import ru.said.miami.orm.core.query.core.clause.GroupBy;
import ru.said.miami.orm.core.query.core.clause.Having;
import ru.said.miami.orm.core.query.core.clause.from.FromJoinedTables;
import ru.said.miami.orm.core.query.core.clause.from.FromSubQuery;
import ru.said.miami.orm.core.query.core.clause.from.FromTable;
import ru.said.miami.orm.core.query.core.clause.select.SelectAll;
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

    private Where<T> where;

    private Select selectQuery;

    private TableRef tableRef;

    private List<JoinExpression> joinExpressions = new ArrayList<>();

    private List<IHasAlias> hasAliases = new ArrayList<>();

    private Having having;

    private GroupBy groupBy;

    private Select subQuery;

    private Alias alias;

    private DisplayedColumnSpec displayedColumnSpec;

    public QueryBuilder(TableInfo<T> tableInfo) {
        this.alias = new Alias(tableInfo.getTableName());
        this.tableRef = new TableRef(tableInfo.getTableName());
        this.selectQuery = new Select();
        this.where = new Where<>(this, selectQuery.getWhere());
    }

    void addHasAlias(IHasAlias columnSpec) {
        hasAliases.add(columnSpec);
    }

    public void setAlias(String alias) {
        this.alias.setAlias(alias);

        if (tableRef.getAlias() == null) {
           tableRef.alias(this.alias);
        }
    }

    Alias accessAlias() {
        return alias;
    }

    public String getAlias() {
        return alias.getAlias();
    }

    public FunctionWhere<T> function() {
        return new FunctionWhere<>(this, new Expression());
    }

    public HavingWhere<T> having() {
        if (having == null) {
            having = new Having();
        }

        return new HavingWhere<>(this, having.getExpression());
    }

    public QueryBuilder<T> groupBy(String ... columns) {
        if (groupBy == null) {
            groupBy = new GroupBy();
        }
        for (String name: columns) {
            groupBy.add(new ColumnSpec(name).alias(alias));
        }

        return this;
    }

    public CountAll countAll() {
        return new CountAll();
    }

    public JoinWhere<T> leftJoin(QueryBuilder<?> queryBuilder) {
        LeftJoin leftJoin = new LeftJoin(queryBuilder.tableRef);
        JoinWhere<T> joinWhere = new JoinWhere<>(this, queryBuilder, leftJoin.getExpression());

        joinExpressions.add(leftJoin);

        return joinWhere;
    }

    public SelectColumnsBuilder<T> selectColumns() {
        if (displayedColumnSpec == null) {
            displayedColumnSpec = new DisplayedOperand();
            ((DisplayedOperand) displayedColumnSpec).alias(alias);
        }

        return new SelectColumnsBuilder<>(this, ((DisplayedOperand) displayedColumnSpec).getOperand());
    }

    public QueryBuilder<T> selectColumns(String ... columns) {
        if (displayedColumnSpec == null) {
            displayedColumnSpec = new DisplayedColumns();
        }
        for (String name: columns) {
            ((DisplayedColumns) displayedColumnSpec).addColumn(new ColumnSpec(name).alias(alias));
        }

        return this;
    }

    public QueryBuilder<T> fromSubQuery(QueryBuilder<?> queryBuilder) {
        queryBuilder.buildSelect();
        subQuery = queryBuilder.getSelect();

        return this;
    }

    public Where<T> where() {
        return where;
    }

    private void buildSelect() {
        if (joinExpressions.size() > 0) {
            FromJoinedTables fromJoinedTables = new FromJoinedTables(tableRef);

            fromJoinedTables.getJoinExpression().addAll(joinExpressions);
            selectQuery.setFrom(fromJoinedTables);
        } else if (subQuery != null) {
            selectQuery.setFrom(new FromSubQuery(subQuery));
        } else {
            selectQuery.setFrom(new FromTable(tableRef));
        }
        if (having != null) {
            selectQuery.setHaving(having);
        }
        if (groupBy != null) {
            selectQuery.setGroupBy(groupBy);
        }
        selectQuery.setSelectColumnsStrategy(new SelectAll());
    }

    Select getSelect() {
        return selectQuery;
    }

    public PreparedQuery prepare() throws SQLException {
        buildSelect();
        DefaultVisitor defaultVisitor = new DefaultVisitor();

        selectQuery.accept(defaultVisitor);
        return new PreparedQuery(defaultVisitor.getQuery());
    }
}
