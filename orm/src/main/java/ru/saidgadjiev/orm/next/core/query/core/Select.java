package ru.saidgadjiev.orm.next.core.query.core;

import ru.saidgadjiev.orm.next.core.query.core.clause.GroupBy;
import ru.saidgadjiev.orm.next.core.query.core.clause.Having;
import ru.saidgadjiev.orm.next.core.query.core.clause.OrderBy;
import ru.saidgadjiev.orm.next.core.query.core.clause.from.FromExpression;
import ru.saidgadjiev.orm.next.core.query.core.clause.from.FromTable;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectAll;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectColumnsStrategy;
import ru.saidgadjiev.orm.next.core.query.core.common.TableRef;
import ru.saidgadjiev.orm.next.core.query.core.condition.Expression;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.orm.next.core.table.internal.metamodel.DatabaseEntityMetadata;

/**
 * Класс SELECT запроса
 */
public class Select implements QueryElement {

    private FromExpression from;

    private SelectionMode selectionMode;

    private SelectColumnsStrategy selectColumnsStrategy;

    private Expression where = new Expression();

    private OrderBy orderBy;

    private GroupBy groupBy;

    private Having having;

    private Limit limit;

    private Offset offset;

    public FromExpression getFrom() {
        return from;
    }

    public void setFrom(FromExpression from) {
        this.from = from;
    }

    public Expression getWhere() {
        return where;
    }

    public void setWhere(Expression where) {
        this.where = where;
    }

    public SelectColumnsStrategy getSelectColumnsStrategy() {
        return selectColumnsStrategy;
    }

    public GroupBy getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(GroupBy groupBy) {
        this.groupBy = groupBy;
    }

    public void setSelectColumnsStrategy(SelectColumnsStrategy selectColumnsStrategy) {
        this.selectColumnsStrategy = selectColumnsStrategy;
    }

    public Having getHaving() {
        return having;
    }

    public void setHaving(Having having) {
        this.having = having;
    }

    public SelectionMode getSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public void setLimit(Limit limit) {
        this.limit = limit;
    }

    public void setOffset(Offset offset) {
        this.offset = offset;
    }

    public void appendByIdClause(DatabaseEntityMetadata<?> entityMetadata, String tableAlias, Object id) {

    }

    public static Select buildQueryForAll(String typeName) {
        Select selectQuery = new Select();

        selectQuery.setFrom(new FromTable(new TableRef(typeName)));
        selectQuery.setSelectColumnsStrategy(new SelectAll());

        return selectQuery;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            selectColumnsStrategy.accept(visitor);
            from.accept(visitor);
            if (where != null) {
                where.accept(visitor);
            }
            if (groupBy != null) {
                groupBy.accept(visitor);
            }
            if (orderBy != null) {
                orderBy.accept(visitor);
            }
            if (having != null) {
                having.accept(visitor);
            }
            if (limit != null) {
                limit.accept(visitor);
            }
            if (offset != null) {
                offset.accept(visitor);
            }
        }
    }

    public Limit getLimit() {
        return limit;
    }

    public Offset getOffset() {
        return offset;
    }

    enum SelectionMode {
        DISTINCT
    }

}
