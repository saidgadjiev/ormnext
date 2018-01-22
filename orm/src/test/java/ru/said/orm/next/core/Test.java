package ru.said.orm.next.core;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Assert;
import org.sqlite.SQLiteDataSource;
import ru.said.orm.next.core.dao.Dao;
import ru.said.orm.next.core.dao.DaoManager;
import ru.said.orm.next.core.db.H2DatabaseType;
import ru.said.orm.next.core.db.SQLiteDatabaseType;
import ru.said.orm.next.core.field.DBField;
import ru.said.orm.next.core.support.DataSourceConnectionSource;

import javax.sql.DataSource;

public class Test {

    @org.junit.Test
    public void testH2DB() throws Exception {
        JdbcDataSource dataSource = new JdbcDataSource();

        dataSource.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        Dao<TestClazz, Integer> dao = DaoManager.createDAO(new DataSourceConnectionSource(dataSource, new H2DatabaseType()), TestClazz.class);

        Assert.assertTrue(dao.createTable(true));
        Assert.assertNull(dao.queryForId(0));
    }

    @org.junit.Test
    public void testSQLite() throws Exception {
        SQLiteDataSource dataSource = new SQLiteDataSource();

        dataSource.setUrl("jdbc:sqlite:C:/test.sqlite");
        Dao<TestClazz, Integer> dao = DaoManager.createDAO(new DataSourceConnectionSource(dataSource, new SQLiteDatabaseType()), TestClazz.class);

        Assert.assertTrue(dao.createTable(true));
        Assert.assertNull(dao.queryForId(0));
    }

    public static class TestClazz {
        @DBField(id = true, generated = true)
        private int id;
    }
}
