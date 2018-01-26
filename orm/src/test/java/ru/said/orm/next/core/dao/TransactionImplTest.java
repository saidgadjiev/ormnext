package ru.said.orm.next.core.dao;

import org.junit.Assert;
import org.junit.Test;
import ru.said.orm.next.core.db.H2DatabaseType;
import ru.said.orm.next.core.field.DBField;
import ru.said.orm.next.core.support.ConnectionSource;
import ru.said.orm.next.core.support.JDBCConnectionSource;

import java.sql.SQLException;

public class TransactionImplTest {

    @Test
    public void commitTrans() throws Exception {
        Dao<TestClazz, Integer> dao = createDao();
        TransactionImpl<TestClazz, Integer> transaction = dao.transaction();
        TestClazz testClazz = new TestClazz();

        testClazz.name = "Test";
        dao.createTable(true);

        transaction.beginTrans();
        transaction.create(testClazz);
        transaction.rollback();

        Assert.assertEquals(0, dao.queryForAll().size());

        transaction.beginTrans();
        transaction.create(testClazz);
        transaction.commitTrans();

        Assert.assertEquals(1, dao.queryForAll().size());

        transaction.close();
    }

    private Dao<TestClazz, Integer> createDao() throws SQLException {
        ConnectionSource connectionSource = new JDBCConnectionSource("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", new H2DatabaseType());
        Dao<TestClazz, Integer> dao = DaoManager.createDAO(connectionSource, TestClazz.class);

        return dao;
    }

    private static class TestClazz {
        @DBField(id = true, generated = true)
        private int id;

        @DBField
        private String name;
    }
}