package ru.saidgadjiev.orm.next.core.criteria;

import org.junit.Test;
import ru.saidgadjiev.orm.next.core.db.H2DatabaseType;
import ru.saidgadjiev.orm.next.core.query.core.Select;
import ru.saidgadjiev.orm.next.core.query.visitor.DefaultVisitor;

import static org.junit.Assert.*;

public class CriteriaTest {

    @Test
    public void add() {
        SelectCriteria select = new SelectCriteria("Test");
        Criteria where = new Criteria()
                .add(Restrictions.eq("test",2))
                .or()
                .add(Restrictions.eq("test1", 0));

        select.setWhere(where);
        Criteria having = new Criteria()
                .add(Restrictions.eq(Projections.sum("test"),2));

        select.setHaving(having);
        select.addOrderBy(Order.orderAsc("test", "test1"));

        DefaultVisitor visitor = new DefaultVisitor(new H2DatabaseType());

        select.accept(visitor);

        System.out.println(visitor.getQuery());
        System.out.println(select.collectArgs());
    }

    @Test
    public void or() {
    }
}