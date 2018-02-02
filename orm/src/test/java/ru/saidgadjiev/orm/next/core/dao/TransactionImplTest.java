package ru.saidgadjiev.orm.next.core.dao;

import org.junit.Assert;
import org.junit.Test;
import ru.saidgadjiev.orm.next.core.db.H2DatabaseType;
import ru.saidgadjiev.orm.next.core.field.DBField;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.support.JDBCConnectionSource;

import java.sql.SQLException;

public class TransactionImplTest {

    @Test
    public void commitTrans() throws Exception {
        Dao<TestClazz, Integer> dao = createDao();
        TransactionImpl<TestClazz, Integer> transaction = dao.transaction();
        TestClazz testClazz = new TestClazz();

        testClazz.name = "Test";
        dao.createTable(true);

        transaction.begin();
        transaction.create(testClazz);
        transaction.rollback();

        Assert.assertEquals(0, dao.queryForAll().size());

        transaction.begin();
        transaction.create(testClazz);
        transaction.commit();

        Assert.assertEquals(1, dao.queryForAll().size());

        transaction.close();
    }

    private Dao<TestClazz, Integer> createDao() throws SQLException {
        ConnectionSource connectionSource = new JDBCConnectionSource("jdbc:h2:mem:test", new H2DatabaseType());
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