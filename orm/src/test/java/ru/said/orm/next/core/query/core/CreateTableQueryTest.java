package ru.said.orm.next.core.query.core;

import org.junit.Assert;
import org.junit.Test;
import ru.said.orm.next.core.dao.Dao;
import ru.said.orm.next.core.dao.DaoManager;
import ru.said.orm.next.core.dao.TransactionImplTest;
import ru.said.orm.next.core.db.H2DatabaseType;
import ru.said.orm.next.core.field.DBField;
import ru.said.orm.next.core.query.visitor.DefaultVisitor;
import ru.said.orm.next.core.query.visitor.QueryVisitor;
import ru.said.orm.next.core.support.ConnectionSource;
import ru.said.orm.next.core.support.JDBCConnectionSource;
import ru.said.orm.next.core.table.DBTable;
import ru.said.orm.next.core.table.Index;
import ru.said.orm.next.core.table.TableInfo;
import ru.said.orm.next.core.table.Unique;

import java.sql.SQLException;

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
        QueryVisitor visitor = new DefaultVisitor(new H2DatabaseType());

        query.accept(visitor);
        Assert.assertEquals("CREATE TABLE IF NOT EXISTS `test` (" +
                "`id` INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                "`name` VARCHAR(255), " +
                "`foreign_test_id` INTEGER REFERENCES `foreign_table`(`id`), " +
                "UNIQUE (`id`,`foreign_test_id`), " +
                "UNIQUE (`id`,`name`))" , visitor.getQuery());

        query = CreateTableQuery.buildQuery(tableInfo, false);
        visitor = new DefaultVisitor(new H2DatabaseType());

        query.accept(visitor);
        Assert.assertEquals("CREATE TABLE `test` (" +
                "`id` INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                "`name` VARCHAR(255), " +
                "`foreign_test_id` INTEGER REFERENCES `foreign_table`(`id`), " +
                "UNIQUE (`id`,`foreign_test_id`), " +
                "UNIQUE (`id`,`name`))", visitor.getQuery());

        Assert.assertTrue(createDao(TestForeignClazz.class).createTable(true));
        Assert.assertTrue(createDao(TestClazz.class).createTable(true));
    }

    private<T> Dao<T, Integer> createDao(Class<T> clazz) throws SQLException {
        ConnectionSource connectionSource = new JDBCConnectionSource("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", new H2DatabaseType());
        Dao<T, Integer> dao = DaoManager.createDAO(connectionSource, clazz);

        return dao;
    }

    @DBTable(name = "test", uniqueConstraints = {
            @Unique(columns = {"id", "foreignClazz"}),
            @Unique(columns = {"id", "name"})
    })
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