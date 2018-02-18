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
        where.add(Restrictions.in("test_in", create()));
        select.setWhere(where);
        Criteria having = new Criteria()
                .add(Restrictions.eq(Projections.sum("test"),2));

        select.setHaving(having);
        select.addOrderBy(Order.orderAsc("test", "test1"));
        select.createAlias("this_is_sparta");

        System.out.println(select.accept(new DefaultVisitor(new H2DatabaseType())));
        System.out.println(select.collectArgs());
    }

    private SelectCriteria create() {
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
        select.createAlias("this_is_sparta_in_select");

        return select;
    }

    @Test
    public void or() {
    }
}