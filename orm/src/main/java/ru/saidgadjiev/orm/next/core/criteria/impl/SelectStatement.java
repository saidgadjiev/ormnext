package ru.saidgadjiev.orm.next.core.criteria.impl;

import ru.saidgadjiev.orm.next.core.criteria.CriteriaQueryVisitor;
import ru.saidgadjiev.orm.next.core.query.core.Alias;
import ru.saidgadjiev.orm.next.core.query.core.Limit;
import ru.saidgadjiev.orm.next.core.query.core.Offset;
import ru.saidgadjiev.orm.next.core.query.core.Select;
import ru.saidgadjiev.orm.next.core.query.core.clause.Having;
import ru.saidgadjiev.orm.next.core.query.core.clause.OrderBy;
import ru.saidgadjiev.orm.next.core.query.core.clause.from.FromTable;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectAll;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectColumnsStrategy;
import ru.saidgadjiev.orm.next.core.query.core.common.TableRef;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.orm.next.core.table.TableInfo;
import ru.saidgadjiev.orm.next.core.table.TableInfoManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class SelectStatement<T> implements QueryElement {

    private Criteria where;

    private OrderBy orderBy;

    private Criteria having;

    private Map<Integer, Object> args = new HashMap<>();

    private AtomicInteger index = new AtomicInteger();

    private Alias alias;

    private TableInfo<?> tableInfo;

    private boolean argsCollected = false;

    private Limit limit;

    private Offset offset;

    private SelectColumnsStrategy selectColumnsStrategy;

    public SelectStatement(Class<?> tClass) {
        this.tableInfo = TableInfoManager.buildOrGet(tClass);
    }

    public SelectStatement<T> where(Criteria where) {
        this.where = where;

        return this;
    }

    public SelectStatement<T> having(Criteria having) {
        this.having = having;

        return this;
    }

    public SelectStatement<T> orderBy(OrderByList orderByList) {
        this.orderBy = orderByList.create();

        return this;
    }

    public SelectStatement<T> alias(String alias) {
        this.alias = new Alias(alias);

        return this;
    }

    public SelectStatement<T> limit(int limit) {
        this.limit = new Limit(limit);

        return this;
    }

    public SelectStatement<T> offset(int offset) {
        this.offset = new Offset(offset);

        return this;
    }

    public SelectStatement<T> selectProjections(ProjectionList projectionList) {
        this.selectColumnsStrategy = projectionList.create();

        return this;
    }

    public SelectStatement<T> selectAll() {
        this.selectColumnsStrategy = new SelectAll();

        return this;
    }

    Queue<Object> collectArgs() {
        Queue<Object> argsQueue = new LinkedList<>();

        if (where != null) {
            Queue<Object> whereArgs = where.getArgs();

            argsQueue.addAll(whereArgs);
            while (!whereArgs.isEmpty()) {
                args.put(index.incrementAndGet(), whereArgs.poll());
            }
        }
        if (having != null) {
            Queue<Object> havingArgs = having.getArgs();

            argsQueue.addAll(havingArgs);
            while (!havingArgs.isEmpty()) {
                args.put(index.incrementAndGet(), havingArgs.poll());
            }
        }
        argsCollected = true;

        return argsQueue;
    }

    public Map<Integer, Object> getArgs() {
        if (!argsCollected) {
            collectArgs();
        }
        return args;
    }

    public Alias getAlias() {
        return alias;
    }

    Select prepareSelect() {
        Select select = new Select();

        select.setSelectColumnsStrategy(new SelectAll());
        select.setFrom(new FromTable(new TableRef(tableInfo.getTableName())));
        if (where != null) {
            select.setWhere(where.getExpression());
        }
        if (having != null) {
            select.setHaving(new Having(having.getExpression()));
        }
        if (orderBy != null) {
            select.setOrderBy(orderBy);
        }
        if (limit != null) {
            select.setLimit(limit);
        }
        if (offset != null) {
            select.setOffset(offset);
        }
        if (selectColumnsStrategy != null) {
            select.setSelectColumnsStrategy(selectColumnsStrategy);
        }

        select.accept(new CriteriaQueryVisitor(tableInfo, alias));

        return select;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        Select select = prepareSelect();

        select.accept(visitor);
    }
}
