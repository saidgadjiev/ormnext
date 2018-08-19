package ru.saidgadjiev.ormnext.core.query.criteria.impl;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.dao.SessionCriteriaContract;
import ru.saidgadjiev.ormnext.core.query.visitor.element.Limit;
import ru.saidgadjiev.ormnext.core.query.visitor.element.Offset;
import ru.saidgadjiev.ormnext.core.query.visitor.element.clause.*;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.DisplayedColumnSpec;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.DisplayedOperand;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.DisplayedPropertyColumn;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.PropertyColumnSpec;
import ru.saidgadjiev.ormnext.core.query.visitor.element.condition.Expression;
import ru.saidgadjiev.ormnext.core.query.visitor.element.function.CountAll;
import ru.saidgadjiev.ormnext.core.query.visitor.element.function.CountColumn;
import ru.saidgadjiev.ormnext.core.query.visitor.element.function.Function;

import java.sql.SQLException;
import java.util.*;

/**
 * This class represent select executeQuery.
 *
 * @param <T> entity type
 * @author Said Gadjiev
 */
public class SelectStatement<T> implements CriteriaStatement {

    /**
     * Target entity class.
     */
    private final Class<T> entityClass;

    /**
     * Session.
     */
    private SessionCriteriaContract session;

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
    private Collection<DisplayedColumnSpec> selectOperands = new ArrayList<>();

    /**
     * Provided user args.
     */
    private final Map<Integer, Object> userProvidedArgs = new HashMap<>();

    /**
     * Without joins if true.
     */
    private boolean withoutJoins = false;

    /**
     * Create a new instance.
     *
     * @param entityClass target entity class
     * @param session     target session
     */
    public SelectStatement(Class<T> entityClass, SessionCriteriaContract session) {
        this.entityClass = entityClass;
        this.session = session;
    }

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
    public SelectStatement<T> offset(long offset) {
        this.offset = new Offset(offset);

        return this;
    }

    /**
     * Set select count star.
     *
     * @return this for chain
     */
    public SelectStatement<T> countOff() {
        selectOperands.add(new DisplayedOperand(new CountAll()));

        return this;
    }

    /**
     * Set select count(column_name).
     *
     * @param property target property
     * @return this for chain
     */
    public SelectStatement<T> countOff(String property) {
        selectOperands.add(new DisplayedOperand(new CountColumn(new PropertyColumnSpec(property))));

        return this;
    }

    /**
     * Add select property. It must be from main table.
     *
     * @param property target property
     * @return this for chain
     */
    public SelectStatement<T> select(String property) {
        selectOperands.add(new DisplayedPropertyColumn(new PropertyColumnSpec(property)));

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

    @Override
    public Map<Integer, Object> getUserProvidedArgs() {
        return userProvidedArgs;
    }

    /**
     * Return entity class.
     *
     * @return entity class
     */
    public Class<T> getEntityClass() {
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
    public Collection<DisplayedColumnSpec> getSelectOperands() {
        return selectOperands;
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
     * Execute for unique result.
     *
     * @return unique result
     * @throws SQLException any SQL exceptions
     */
    public T uniqueResult() throws SQLException {
        return session.uniqueResult(this);
    }

    /**
     * Retrieve beans list.
     *
     * @return beans list
     * @throws SQLException any SQL exceptions
     */
    public List<T> list() throws SQLException {
        return session.list(this);
    }

    /**
     * Query for long.
     *
     * @return long result
     * @throws SQLException any SQL exceptions
     */
    public Long queryForLong() throws SQLException {
        return session.queryForLong(this);
    }

    /**
     * Execute for results.
     *
     * @return query results
     * @throws SQLException any SQL exceptions
     */
    public DatabaseResults executeQuery() throws SQLException {
        return session.query(this);
    }

    @Override
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
    public void attach(Session session) {
        this.session = (SessionCriteriaContract) session;
    }
}
