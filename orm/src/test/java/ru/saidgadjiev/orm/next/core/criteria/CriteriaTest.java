package ru.saidgadjiev.orm.next.core.criteria;

import org.junit.Test;
import ru.saidgadjiev.orm.next.core.criteria.impl.Criteria;
import ru.saidgadjiev.orm.next.core.criteria.impl.Projections;
import ru.saidgadjiev.orm.next.core.criteria.impl.Restrictions;
import ru.saidgadjiev.orm.next.core.criteria.impl.SelectCriteria;
import ru.saidgadjiev.orm.next.core.db.H2DatabaseType;
import ru.saidgadjiev.orm.next.core.field.DBField;
import ru.saidgadjiev.orm.next.core.field.DataType;
import ru.saidgadjiev.orm.next.core.query.visitor.DefaultVisitor;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

public class CriteriaTest {

    @Test
    public void add() throws Exception {
        SelectCriteria<TestClazz> selectCriteria = new SelectCriteria<>(TableInfo.build(TestClazz.class));
        Criteria where = new Criteria().add(Restrictions.eq("id", 1)).add(Restrictions.in("name", createSubQuery()));

        selectCriteria.setWhere(where);
        DefaultVisitor visitor = new DefaultVisitor(new H2DatabaseType());

        selectCriteria.createAlias("test_alias");
        selectCriteria.setHaving(new Criteria().add(Restrictions.eq(Projections.sum("name"), 2)));

        selectCriteria.accept(visitor);
        System.out.println(visitor.getQuery());
    }

    private SelectCriteria<TestClazz> createSubQuery() throws Exception {
        SelectCriteria<TestClazz> selectCriteria = new SelectCriteria<>(TableInfo.build(TestClazz.class));

        selectCriteria.createAlias("test_alias_in_sub_query");
        selectCriteria.setWhere(new Criteria().add(Restrictions.eq("name", "said")));

        return selectCriteria;
    }

    @Test
    public void or() {
    }

    public static class TestClazz {
        @DBField(id = true, generated = true)
        private int id;

        @DBField(dataType = DataType.STRING, columnName = "test_name")
        private String name;
    }
}