package ru.saidgadjiev.ormnext.core.criteria.impl;

import ru.saidgadjiev.ormnext.core.query.core.Limit;
import ru.saidgadjiev.ormnext.core.query.core.Offset;
import ru.saidgadjiev.ormnext.core.query.core.clause.GroupBy;
import ru.saidgadjiev.ormnext.core.query.core.clause.Having;
import ru.saidgadjiev.ormnext.core.query.core.clause.OrderBy;
import ru.saidgadjiev.ormnext.core.query.core.condition.Expression;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.core.Limit;
import ru.saidgadjiev.ormnext.core.query.core.Offset;
import ru.saidgadjiev.ormnext.core.query.core.clause.GroupBy;
import ru.saidgadjiev.ormnext.core.query.core.clause.Having;
import ru.saidgadjiev.ormnext.core.query.core.clause.OrderBy;
import ru.saidgadjiev.ormnext.core.query.core.condition.Expression;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("CPD-START")
public class CriteriaQuery<T> implements QueryElement {

    private final Class<T> persistentClass;

    private Criteria where;

    private OrderBy orderBy;

    private GroupBy groupBy;

    private Criteria having;

    private Limit limit;

    private Offset offset;

    private Map<Integer, Object> userProvidedArgs = new HashMap<>();

    public CriteriaQuery(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    public CriteriaQuery<T> where(Criteria where) {
        this.where = where;

        return this;
    }

    public CriteriaQuery<T> having(Criteria having) {
        this.having = having;

        return this;
    }

    public CriteriaQuery<T> orderBy(OrderByCriteria orderByCriteria) {
        this.orderBy = orderByCriteria.create();

        return this;
    }

    public CriteriaQuery<T> groupBy(GroupByCriteria groupByCriteria) {
        this.groupBy = groupByCriteria.create();

        return this;
    }

    public CriteriaQuery<T> limit(int limit) {
        this.limit = new Limit(limit);

        return this;
    }

    public CriteriaQuery<T> offset(int offset) {
        this.offset = new Offset(offset);

        return this;
    }

    public CriteriaQuery<T> addArg(int index, Object arg) {
        userProvidedArgs.put(index, arg);

        return this;
    }

    public List<Object> getArgs() {
        List<Object> args = new LinkedList<>();

        if (where != null) {
            args.addAll(where.getArgs());
        }
        if (having != null) {
            args.addAll(having.getArgs());
        }

        for (Map.Entry<Integer, Object> entry: userProvidedArgs.entrySet()) {
            args.add(entry.getKey() - 1, entry.getValue());
        }

        return args;
    }

    public Class<?> getPersistentClass() {
        return persistentClass;
    }

    public Expression getWhere() {
        return where == null ? null : where.getWhere();
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public GroupBy getGroupBy() {
        return groupBy;
    }

    public Having getHaving() {
        return having == null ? null : new Having(having.getWhere());
    }

    public Limit getLimit() {
        return limit;
    }

    public Offset getOffset() {
        return offset;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (where != null) {
            where.getWhere().accept(visitor);
        }
        if (orderBy != null) {
            orderBy.accept(visitor);
        }
        if (groupBy != null) {
            groupBy.accept(visitor);
        }
        if (having != null) {
            having.getWhere().accept(visitor);
        }
    }
}
