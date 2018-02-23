package ru.saidgadjiev.orm.next.core.criteria;

import ru.saidgadjiev.orm.next.core.query.core.Alias;
import ru.saidgadjiev.orm.next.core.query.core.Select;
import ru.saidgadjiev.orm.next.core.query.core.clause.Having;
import ru.saidgadjiev.orm.next.core.query.core.clause.OrderBy;
import ru.saidgadjiev.orm.next.core.query.core.clause.OrderByItem;
import ru.saidgadjiev.orm.next.core.query.core.clause.from.FromTable;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectAll;
import ru.saidgadjiev.orm.next.core.query.core.common.TableRef;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class SelectCriteria<T> {

    private Criteria where;

    private OrderBy orderBy = new OrderBy();

    private Criteria having;

    private Map<Integer, Object> args = new HashMap<>();

    private AtomicInteger index = new AtomicInteger();

    private Alias alias;

    private TableInfo<T> tableInfo;

    public SelectCriteria(TableInfo<T> tableInfo) {
        this.tableInfo = tableInfo;
    }

    public void setWhere(Criteria where) {
        this.where = where;
    }

    public void setHaving(Criteria having) {
        this.having = having;
    }

    public void addOrderBy(OrderByItem orderByItem) {
        orderBy.add(orderByItem);
    }

    public void createAlias(String alias) {
        this.alias = new Alias(alias);
    }

    public Collection<Object> collectArgs() {
        if (where != null) {
            Queue<Object> whereArgs = where.getArgs();

            while (!whereArgs.isEmpty()) {
                args.put(index.incrementAndGet(), whereArgs.poll());
            }
        }
        if (having != null) {
            Queue<Object> havingArgs = having.getArgs();

            while (!havingArgs.isEmpty()) {
                args.put(index.incrementAndGet(), havingArgs.poll());
            }
        }

        return args.values();
    }

    public Alias getAlias() {
        return alias;
    }

    Select prepareSelect() {
        collectArgs();
        Select select = new Select();

        select.setSelectColumnsStrategy(new SelectAll());
        select.setFrom(new FromTable(new TableRef(tableInfo.getTableName())));
        if (where != null) {
            select.setWhere(where.getExpression());
        }
        if (having != null) {
            select.setHaving(new Having(having.getExpression()));
        }
        if (!orderBy.getOrderByItems().isEmpty()) {
            select.setOrderBy(orderBy);
        }

        select.accept(new CriteriaQueryVisitor(tableInfo, alias));

        return select;
    }

    public String accept(QueryVisitor visitor) {
        Select select = prepareSelect();

        select.accept(visitor);

        return visitor.getQuery();
    }
}
