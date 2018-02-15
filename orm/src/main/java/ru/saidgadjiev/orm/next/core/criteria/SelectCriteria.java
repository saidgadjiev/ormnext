package ru.saidgadjiev.orm.next.core.criteria;

import ru.saidgadjiev.orm.next.core.query.core.Select;
import ru.saidgadjiev.orm.next.core.query.core.clause.Having;
import ru.saidgadjiev.orm.next.core.query.core.clause.OrderBy;
import ru.saidgadjiev.orm.next.core.query.core.clause.OrderByItem;
import ru.saidgadjiev.orm.next.core.query.core.clause.from.FromExpression;
import ru.saidgadjiev.orm.next.core.query.core.clause.from.FromTable;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectAll;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectColumnsStrategy;
import ru.saidgadjiev.orm.next.core.query.core.common.TableRef;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class SelectCriteria implements CriteriaElement {

    private Select select = new Select();

    private Criteria where;

    private OrderBy orderBy = new OrderBy();

    private Criteria having;

    private Having preparedHaving;

    private Map<Integer, Object> args = new HashMap<>();

    private AtomicInteger index = new AtomicInteger();

    public SelectCriteria(String tableName) {
        select.setFrom(new FromTable(new TableRef(tableName)));
        select.setSelectColumnsStrategy(new SelectAll());
    }

    public void setWhere(Criteria where) {
        this.where = where;
    }

    public void setHaving(Criteria having) {
        this.having = having;
        this.preparedHaving = new Having(having.getExpression());
    }

    public void addOrderBy(OrderByItem orderByItem) {
        orderBy.add(orderByItem);
    }

    public Criteria getWhere() {
        return where;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public Having getHaving() {
        return preparedHaving;
    }

    public Map<Integer, Object> collectArgs() {
        Queue<Object> whereArgs = where.getArgs();

        while (!whereArgs.isEmpty()) {
            args.put(index.incrementAndGet(), whereArgs.poll());
        }
        Queue<Object> havingArgs = having.getArgs();

        while (!havingArgs.isEmpty()) {
            args.put(index.incrementAndGet(), havingArgs.poll());
        }

        return args;
    }

    @Override
    public void accept(CriteriaVisitor visitor) {
        visitor.start(this);
        if (where != null) {
            where.accept(visitor);
        }
        if (!orderBy.getOrderByItems().isEmpty()) {
            orderBy.accept(visitor);
        }
        if (having != null) {
            having.accept(visitor);
        }
        visitor.finish(this);
    }
}
