package ru.saidgadjiev.ormnext.core.query.core;

import ru.saidgadjiev.ormnext.core.query.core.clause.GroupBy;
import ru.saidgadjiev.ormnext.core.query.core.clause.Having;
import ru.saidgadjiev.ormnext.core.query.core.clause.OrderBy;
import ru.saidgadjiev.ormnext.core.query.core.clause.from.FromExpression;
import ru.saidgadjiev.ormnext.core.query.core.clause.from.FromTable;
import ru.saidgadjiev.ormnext.core.query.core.clause.select.SelectAll;
import ru.saidgadjiev.ormnext.core.query.core.clause.select.SelectColumnsStrategy;
import ru.saidgadjiev.ormnext.core.query.core.common.TableRef;
import ru.saidgadjiev.ormnext.core.query.core.condition.Expression;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;

/**
 * This class represent select query. For example it will be visited with {@link ru.saidgadjiev.ormnext.core.query.visitor.DefaultVisitor} for make sql.
 */
public class Select implements QueryElement {

    /**
     * Select from part.
     */
    private FromExpression from;

    /**
     * Select is distinct.
     */
    private boolean distinct;

    /**
     * Select column strategy.
     * @see ru.saidgadjiev.ormnext.core.query.core.clause.select.SelectColumnsList
     * @see SelectAll
     */
    private SelectColumnsStrategy selectColumnsStrategy;

    /**
     * Where clause part.
     */
    private Expression where = new Expression();

    /**
     * Order by part.
     */
    private OrderBy orderBy;

    /**
     * Group by part.
     */
    private GroupBy groupBy;

    /**
     * Having part.
     */
    private Having having;

    /**
     * Limit part.
     */
    private Limit limit;

    /**
     * Offset part.
     */
    private Offset offset;

    /**
     * Return current from part.
     * @return current from part
     */
    public FromExpression getFrom() {
        return from;
    }

    /**
     * Provide from part.
     * @param from target from
     */
    public void setFrom(FromExpression from) {
        this.from = from;
    }

    /**
     * Return current where part.
     * @return current where part
     */
    public Expression getWhere() {
        return where;
    }

    /**
     * Provide where part.
     * @param where target where
     */
    public void setWhere(Expression where) {
        this.where = where;
    }

    /**
     * Return current selectColumns strategy part.
     * @return current selectColumns strategy part
     */
    public SelectColumnsStrategy getSelectColumnsStrategy() {
        return selectColumnsStrategy;
    }

    /**
     * Return current group by part.
     * @return current group by part
     */
    public GroupBy getGroupBy() {
        return groupBy;
    }

    /**
     * Provide addGroupBy part.
     * @param groupBy target group by
     */
    public void setGroupBy(GroupBy groupBy) {
        this.groupBy = groupBy;
    }

    /**
     * Provide selectColumnsStrategy part.
     * @param selectColumnsStrategy target selectColumns strategy
     */
    public void setSelectColumnsStrategy(SelectColumnsStrategy selectColumnsStrategy) {
        this.selectColumnsStrategy = selectColumnsStrategy;
    }

    /**
     * Return current having part.
     * @return current having part
     */
    public Having getHaving() {
        return having;
    }

    /**
     * Provide having part.
     * @param having target having
     */
    public void setHaving(Having having) {
        this.having = having;
    }

    /**
     * Return is select distinct.
     * @return is select distinct
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * Provide distinct.
     * @param distinct target distinct
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * Return current order by part.
     * @return current order by part.
     */
    public OrderBy getOrderBy() {
        return orderBy;
    }

    /**
     * Provide addOrder part.
     * @param orderBy target addOrder
     */
    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * Provide limit part.
     * @param limit target limit
     */
    public void setLimit(Limit limit) {
        this.limit = limit;
    }

    /**
     * Return current limit part.
     * @return limit
     */
    public Limit getLimit() {
        return limit;
    }

    /**
     * Provide offset part.
     * @param offset target offset
     */
    public void setOffset(Offset offset) {
        this.offset = offset;
    }

    /**
     * Return current offset part.
     * @return limit
     */
    public Offset getOffset() {
        return offset;
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

}
