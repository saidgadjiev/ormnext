package ru.saidgadjiev.orm.next.core.criteria;

import ru.saidgadjiev.orm.next.core.query.core.Select;
import ru.saidgadjiev.orm.next.core.query.core.clause.Having;
import ru.saidgadjiev.orm.next.core.query.core.clause.OrderBy;
import ru.saidgadjiev.orm.next.core.query.core.clause.OrderByItem;
import ru.saidgadjiev.orm.next.core.query.core.clause.from.FromTable;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectAll;
import ru.saidgadjiev.orm.next.core.query.core.common.TableRef;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class SelectCriteria implements QueryElement {

    private Select select = new Select();

    private Criteria where;

    private OrderBy orderBy = new OrderBy();

    private Criteria having;

    private Map<Integer, Object> args = new HashMap<>();

    private AtomicInteger index = new AtomicInteger();

    private String tableName;

    public SelectCriteria(String tableName) {
        this.tableName = tableName;
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

    private void prepareSelect() {
        collectArgs();
        select.setSelectColumnsStrategy(new SelectAll());
        select.setFrom(new FromTable(new TableRef(tableName)));
        select.setWhere(where.getExpression());
        select.setOrderBy(orderBy);
        select.setHaving(new Having(having.getExpression()));
    }

    @Override
    public void accept(QueryVisitor visitor) {
        prepareSelect();
        select.accept(visitor);
    }
}
