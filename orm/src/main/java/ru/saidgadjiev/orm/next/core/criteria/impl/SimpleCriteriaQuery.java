package ru.saidgadjiev.orm.next.core.criteria.impl;

import ru.saidgadjiev.orm.next.core.query.core.Limit;
import ru.saidgadjiev.orm.next.core.query.core.Offset;
import ru.saidgadjiev.orm.next.core.query.core.clause.GroupBy;
import ru.saidgadjiev.orm.next.core.query.core.clause.Having;
import ru.saidgadjiev.orm.next.core.query.core.clause.OrderBy;
import ru.saidgadjiev.orm.next.core.query.core.columnspec.ColumnSpec;
import ru.saidgadjiev.orm.next.core.query.core.condition.Expression;
import ru.saidgadjiev.orm.next.core.query.core.function.CountAll;
import ru.saidgadjiev.orm.next.core.query.core.function.CountColumn;
import ru.saidgadjiev.orm.next.core.query.core.function.Function;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//TODO: добавление groupBy для других таблиц
public class SimpleCriteriaQuery implements QueryElement {

    private final Class<?> persistentClass;

    private Function function;

    private Criteria where;

    private OrderBy orderBy;

    private GroupBy groupBy;

    private Criteria having;

    private Limit limit;

    private Offset offset;

    private Map<Integer, Object> userProvidedArgs = new HashMap<>();

    public SimpleCriteriaQuery(Class<?> persistentClass) {
        this.persistentClass = persistentClass;
    }

    public SimpleCriteriaQuery where(Criteria where) {
        this.where = where;

        return this;
    }

    public SimpleCriteriaQuery having(Criteria having) {
        this.having = having;

        return this;
    }

    public SimpleCriteriaQuery orderBy(OrderByCriteria orderByCriteria) {
        this.orderBy = orderByCriteria.create();

        return this;
    }

    public SimpleCriteriaQuery groupBy(GroupByCriteria groupByCriteria) {
        this.groupBy = groupByCriteria.create();

        return this;
    }

    public SimpleCriteriaQuery limit(int limit) {
        this.limit = new Limit(limit);

        return this;
    }

    public SimpleCriteriaQuery offset(int offset) {
        this.offset = new Offset(offset);

        return this;
    }

    public SimpleCriteriaQuery countOff(String propertyName) {
        function = new CountColumn(new ColumnSpec(propertyName));

        return this;
    }

    public SimpleCriteriaQuery countAll() {
        function = new CountAll();

        return this;
    }

    public Function getFunction() {
        return function;
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

    public SimpleCriteriaQuery addArg(int index, Object arg) {
        userProvidedArgs.put(index, arg);

        return this;
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
