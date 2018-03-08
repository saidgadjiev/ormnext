package ru.saidgadjiev.orm.next.core.criteria;

import org.junit.Test;
import ru.saidgadjiev.orm.next.core.criteria.impl.Criteria;
import ru.saidgadjiev.orm.next.core.criteria.impl.Projections;
import ru.saidgadjiev.orm.next.core.criteria.impl.Restrictions;
import ru.saidgadjiev.orm.next.core.criteria.impl.SelectStatement;
import ru.saidgadjiev.orm.next.core.db.H2DatabaseType;
import ru.saidgadjiev.orm.next.core.field.DBField;
import ru.saidgadjiev.orm.next.core.field.DataType;
import ru.saidgadjiev.orm.next.core.query.visitor.DefaultVisitor;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

public class CriteriaTest {

    @Test
    public void add() throws Exception {
        SelectStatement<TestClazz> selectStatement = new SelectStatement<>(TableInfo.build(TestClazz.class));
        Criteria where = new Criteria().add(Restrictions.eq("id", 1)).add(Restrictions.in("name", createSubQuery()));

        selectStatement.setWhere(where);
        DefaultVisitor visitor = new DefaultVisitor(new H2DatabaseType());

        selectStatement.createAlias("test_alias");
        selectStatement.setHaving(new Criteria().add(Restrictions.eq(Projections.sum("name"), 2)));

        selectStatement.accept(visitor);
        System.out.println(visitor.getQuery());
    }

    private SelectStatement<TestClazz> createSubQuery() throws Exception {
        SelectStatement<TestClazz> selectStatement = new SelectStatement<>(TableInfo.build(TestClazz.class));

        selectStatement.createAlias("test_alias_in_sub_query");
        selectStatement.setWhere(new Criteria().add(Restrictions.eq("name", "said")));

        return selectStatement;
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