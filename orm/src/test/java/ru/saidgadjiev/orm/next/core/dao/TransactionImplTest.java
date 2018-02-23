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
        Session<TestClazz, Integer> sessionManager = createDao();
        TransactionImpl<TestClazz, Integer> transaction = sessionManager.transaction();
        TestClazz testClazz = new TestClazz();

        testClazz.name = "Test";
        sessionManager.createTable(true);

        transaction.begin();
        transaction.create(testClazz);
        transaction.rollback();

        Assert.assertEquals(0, sessionManager.queryForAll().size());

        transaction.begin();
        transaction.create(testClazz);
        transaction.commit();

        Assert.assertEquals(1, sessionManager.queryForAll().size());

        transaction.close();
    }

    private Session<TestClazz, Integer> createDao() throws SQLException {
        ConnectionSource connectionSource = new JDBCConnectionSource("jdbc:h2:mem:test", new H2DatabaseType());

        return new BaseSessionManagerImpl(connectionSource).forClass(TestClazz.class);
    }

    private static class TestClazz {
        @DBField(id = true, generated = true)
        private int id;

        @DBField
        private String name;
    }
}