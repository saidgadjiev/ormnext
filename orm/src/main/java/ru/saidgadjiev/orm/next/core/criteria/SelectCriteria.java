package ru.saidgadjiev.orm.next.core.criteria;

import ru.saidgadjiev.orm.next.core.query.core.clause.Having;
import ru.saidgadjiev.orm.next.core.query.core.clause.OrderBy;
import ru.saidgadjiev.orm.next.core.query.core.clause.OrderByItem;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectColumnsStrategy;

public class SelectCriteria implements CriteriaElement {

    SelectColumnsStrategy selectColumnsStrategy;

    Criteria where;

    OrderBy orderBy = new OrderBy();

    Having having;

    public void setWhere(Criteria where) {
        this.where = where;
    }

    public void setHaving(Criteria having) {
        this.having = new Having(having.getExpression());
    }

    public void addOrderBy(OrderByItem orderByItem) {
        orderBy.add(orderByItem);
    }

    public SelectColumnsStrategy getSelectColumnsStrategy() {
        return selectColumnsStrategy;
    }

    public Criteria getWhere() {
        return where;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public Having getHaving() {
        return having;
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
