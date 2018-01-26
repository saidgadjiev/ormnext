package ru.said.orm.next.core;

import org.junit.Assert;
import org.sqlite.SQLiteDataSource;
import ru.said.orm.next.core.dao.Dao;
import ru.said.orm.next.core.dao.DaoManager;
import ru.said.orm.next.core.dao.Transaction;
import ru.said.orm.next.core.db.H2DatabaseType;
import ru.said.orm.next.core.db.SQLiteDatabaseType;
import ru.said.orm.next.core.field.DBField;
import ru.said.orm.next.core.support.DataSourceConnectionSource;
import ru.said.orm.next.core.support.JDBCConnectionSource;

public class Test {

    @org.junit.Test
    public void testH2DB() throws Exception {
        Dao<TestClazz, Integer> dao = DaoManager.createDAO(new JDBCConnectionSource("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", new H2DatabaseType()), TestClazz.class);

        Assert.assertTrue(dao.createTable(false));
        Assert.assertNull(dao.queryForId(0));
        TestClazz testClazz = new TestClazz();

        dao.create(testClazz);
        Assert.assertEquals(1, dao.queryForAll().size());
    }

    @org.junit.Test
    public void testSQLite() throws Exception {
        SQLiteDataSource dataSource = new SQLiteDataSource();

        dataSource.setUrl("jdbc:sqlite:C:/test.sqlite");
        Dao<TestClazz, Integer> dao = DaoManager.createDAO(new DataSourceConnectionSource(dataSource, new SQLiteDatabaseType()), TestClazz.class);

        Assert.assertTrue(dao.createTable(true));
        Assert.assertNull(dao.queryForId(0));

        TestClazz testClazz = new TestClazz();
        dao.create(testClazz);
        Assert.assertEquals(1, dao.queryForAll().size());
    }

    @org.junit.Test
    public void testTransaction() throws Exception {
        SQLiteDataSource dataSource = new SQLiteDataSource();

        dataSource.setUrl("jdbc:sqlite:C:/test.sqlite");
        Dao<TestClazz, Integer> dao = DaoManager.createDAO(new DataSourceConnectionSource(dataSource, new SQLiteDatabaseType()), TestClazz.class);

        Assert.assertTrue(dao.createTable(true));
        Transaction<TestClazz, Integer> transaction = dao.transaction();

        try {
            transaction.begin();
            transaction.commit();
        } catch (Exception ex) {
            transaction.rollback();
        }
        Assert.assertNull(dao.queryForId(0));
    }

    public static class TestClazz {
        @DBField(id = true, generated = true)
        private int id;
    }
}
