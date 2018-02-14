package ru.saidgadjiev.orm.next.core.criteria;

import ru.saidgadjiev.orm.next.core.query.core.clause.Having;
import ru.saidgadjiev.orm.next.core.query.core.clause.OrderBy;
import ru.saidgadjiev.orm.next.core.query.core.clause.OrderByItem;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectColumnsStrategy;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

public class SelectCriteria implements QueryElement {

    SelectColumnsStrategy selectColumnsStrategy;

    Criteria where = new Criteria();

    OrderBy orderBy = new OrderBy();

    Having having;

    public void setWhere(Criteria where) {
        this.where = where;
    }

    public void setHaving(Criteria having) {
        this.having = new Having(having.prepare());
    }

    public void addOrderBy(OrderByItem orderByItem) {
        orderBy.add(orderByItem);
    }

    @Override
    public void accept(QueryVisitor visitor) {
        where.accept(visitor);
        having.accept(visitor);
    }
}
