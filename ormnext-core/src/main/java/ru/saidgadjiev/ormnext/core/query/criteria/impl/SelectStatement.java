package ru.saidgadjiev.ormnext.core.query.criteria.impl;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.Limit;
import ru.saidgadjiev.ormnext.core.query.visitor.element.Offset;
import ru.saidgadjiev.ormnext.core.query.visitor.element.clause.*;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.DisplayedOperand;
import ru.saidgadjiev.ormnext.core.query.visitor.element.condition.Expression;
import ru.saidgadjiev.ormnext.core.query.visitor.element.function.CountAll;
import ru.saidgadjiev.ormnext.core.query.visitor.element.function.CountColumn;
import ru.saidgadjiev.ormnext.core.query.visitor.element.function.Function;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class represent select query.
 *
 * @param <T> entity type
 * @author Said Gadjiev
 */
public class SelectStatement<T> implements QueryElement {

    /**
     * Target entity class.
     */
    private final Class<T> entityClass;

    /**
     * Where expression.
     *
     * @see Criteria
     */
    private Criteria where;

    /**
     * Order by.
     *
     * @see OrderBy
     */
    private OrderBy orderBy;

    /**
     * Group by.
     *
     * @see GroupBy
     */
    private GroupBy groupBy;

    /**
     * Having expression.
     *
     * @see Criteria
     */
    private Criteria having;

    /**
     * Limit.
     *
     * @see Limit
     */
    private Limit limit;

    /**
     * Offset.
     *
     * @see Offset
     */
    private Offset offset;

    /**
     * SelectQuery operand. It may be only {@link Function}
     *
     * @see DisplayedOperand
     */
    private DisplayedOperand selectOperand;

    /**
     * Provided user args.
     */
    private Map<Integer, Object> userProvidedArgs = new HashMap<>();

    /**
     * Without joins if true.
     */
    private boolean withoutJoins = false;

    /**
     * Create a new instance.
     *
     * @param entityClass target entity class
     */
    public SelectStatement(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Provide where expression.
     *
     * @param where target where expression
     * @return this for chain
     */
    public SelectStatement<T> where(Criteria where) {
        this.where = where;

        return this;
    }

    /**
     * Provide having expression.
     *
     * @param having target having expression
     * @return this for chain
     */
    public SelectStatement<T> having(Criteria having) {
        this.having = having;

        return this;
    }

    /**
     * Add new order by item.
     *
     * @param order target order by item
     * @return this for chain
     */
    public SelectStatement<T> addOrder(OrderByItem order) {
        if (orderBy == null) {
            orderBy = new OrderBy();
        }
        orderBy.add(order);

        return this;
    }

    /**
     * Add new group by item.
     *
     * @param group target group by item
     * @return this for chain
     */
    public SelectStatement<T> addGroupBy(GroupByItem group) {
        if (groupBy == null) {
            groupBy = new GroupBy();
        }
        groupBy.add(group);

        return this;
    }

    /**
     * Provide limit value.
     *
     * @param limit target limit
     * @return this for chain
     */
    public SelectStatement<T> limit(int limit) {
        this.limit = new Limit(limit);

        return this;
    }

    /**
     * Provide offset value.
     *
     * @param offset target offset
     * @return this for chain
     */
    public SelectStatement<T> offset(int offset) {
        this.offset = new Offset(offset);

        return this;
    }

    /**
     * Set select count star.
     *
     * @return this for chain
     */
    public SelectStatement<T> countOff() {
        selectOperand = new DisplayedOperand(new CountAll());

        return this;
    }

    /**
     * Set select count(column_name).
     *
     * @param property target property
     * @return this for chain
     */
    public SelectStatement<T> countOff(String property) {
        selectOperand = new DisplayedOperand(new CountColumn(new ColumnSpec(property)));

        return this;
    }

    /**
     * Set true if need select without joins.
     *
     * @param withoutJoins true if need select without joins
     * @return this for chain
     */
    public SelectStatement<T> withoutJoins(boolean withoutJoins) {
        this.withoutJoins = withoutJoins;

        return this;
    }

    /**
     * Set arg for prepared statement.
     *
     * @param index target index
     * @param arg   target arg
     * @return this for chain
     */
    public SelectStatement<T> setObject(int index, Object arg) {
        userProvidedArgs.put(index, arg);

        return this;
    }

    /**
     * Return user provided args.
     *
     * @return user provided args
     */
    public Map<Integer, Object> getUserProvidedArgs() {
        return userProvidedArgs;
    }

    /**
     * Return entity class.
     *
     * @return entity class
     */
    public Class<?> getEntityClass() {
        return entityClass;
    }

    /**
     * Return where expression.
     *
     * @return where expression
     */
    public Expression getWhere() {
        return where == null ? null : where.expression();
    }

    /**
     * Return order by.
     *
     * @return order by
     */

    public OrderBy getOrderBy() {
        return orderBy;
    }

    /**
     * Return group by.
     *
     * @return group by
     */
    public GroupBy getGroupBy() {
        return groupBy;
    }

    /**
     * Return having expression.
     *
     * @return having expression
     */
    public Having getHaving() {
        return having == null ? null : new Having(having.expression());
    }

    /**
     * Return limit.
     *
     * @return limit
     */
    public Limit getLimit() {
        return limit;
    }

    /**
     * Return offset.
     *
     * @return offset
     */
    public Offset getOffset() {
        return offset;
    }

    /**
     * Return select operand. It can be only aggregate function
     *
     * @return select operand
     */
    public DisplayedOperand getSelectOperand() {
        return selectOperand;
    }

    /**
     * Return true if select without joins.
     *
     * @return true if select without joins
     */
    public boolean isWithoutJoins() {
        return withoutJoins;
    }

    /**
     * Return args for prepared statement.
     *
     * @return args for prepared statement
     */
    public List<CriterionArgument> getArgs() {
        List<CriterionArgument> args = new LinkedList<>();

        if (where != null) {
            args.addAll(where.getArgs());
        }
        if (having != null) {
            args.addAll(having.getArgs());
        }

        return args;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (where != null) {
            where.expression().accept(visitor);
        }
        if (orderBy != null) {
            orderBy.accept(visitor);
        }
        if (groupBy != null) {
            groupBy.accept(visitor);
        }
        if (having != null) {
            having.expression().accept(visitor);
        }
    }
}
