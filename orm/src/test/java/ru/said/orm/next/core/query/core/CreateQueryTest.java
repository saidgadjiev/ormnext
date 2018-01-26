package ru.said.orm.next.core.query.core;

import org.junit.Assert;
import org.junit.Test;
import ru.said.orm.next.core.field.DBField;
import ru.said.orm.next.core.query.visitor.DefaultVisitor;
import ru.said.orm.next.core.query.visitor.QueryVisitor;
import ru.said.orm.next.core.table.DBTable;
import ru.said.orm.next.core.table.TableInfo;

/**
 * Created by said on 21.01.2018.
 */
public class CreateQueryTest {
    @Test
    public void buildQuery() throws Exception {
        TableInfo<TestClazz> tableInfo = TableInfo.build(TestClazz.class);
        TestClazz testClazz = new TestClazz();

        testClazz.name = "test";
        TestForeignClazz foreignClazz = new TestForeignClazz();

        foreignClazz.id = 1;
        testClazz.foreignClazz = foreignClazz;
        CreateQuery query = CreateQuery.buildQuery(tableInfo, testClazz);
        QueryVisitor visitor = new DefaultVisitor();

        query.accept(visitor);
        Assert.assertEquals("INSERT INTO 'test' ('id', 'name', 'foreign_test_id') VALUES (0, 'test', 1);", visitor.getQuery());
    }

    @DBTable(name = "test")
    public static class TestClazz {

        @DBField(id = true)
        private int id;

        @DBField
        private String name;

        @DBField(foreign = true, columnName = "foreign_test")
        private TestForeignClazz foreignClazz;
    }

    @DBTable(name = "foreign_table")
    public static class TestForeignClazz {
        @DBField(id = true)
        private int id;

    }
}