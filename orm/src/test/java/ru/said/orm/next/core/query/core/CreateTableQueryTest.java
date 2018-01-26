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
public class CreateTableQueryTest {
    @Test
    public void isIfNotExists() throws Exception {
    }

    @Test
    public void getTypeName() throws Exception {
    }

    @Test
    public void buildQuery() throws Exception {
        TableInfo<TestClazz> tableInfo = TableInfo.build(TestClazz.class);
        CreateTableQuery query = CreateTableQuery.buildQuery(tableInfo, true);
        QueryVisitor visitor = new DefaultVisitor();

        query.accept(visitor);
        Assert.assertEquals("CREATE TABLE IF NOT EXISTS 'test' (" +
                "'id' INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "'name' VARCHAR(255), " +
                "'foreign_test_id' INTEGER REFERENCES 'foreign_table'('id'));", visitor.getQuery());

        query = CreateTableQuery.buildQuery(tableInfo, false);
        visitor = new DefaultVisitor();

        query.accept(visitor);
        Assert.assertEquals("CREATE TABLE 'test' (" +
                "'id' INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "'name' VARCHAR(255), " +
                "'foreign_test_id' INTEGER REFERENCES 'foreign_table'('id'));", visitor.getQuery());
    }

    @DBTable(name = "test")
    public static class TestClazz {

        @DBField(id = true, generated = true)
        private int id;

        @DBField
        private String name;

        @DBField(foreign = true, columnName = "foreign_test")
        private TestForeignClazz foreignClazz;

        public TestClazz() {

        }
    }

    @DBTable(name = "foreign_table")
    public static class TestForeignClazz {
        @DBField(id = true)
        private int id;

        public TestForeignClazz() {

        }
    }

}