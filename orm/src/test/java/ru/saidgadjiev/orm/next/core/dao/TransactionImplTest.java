package ru.saidgadjiev.orm.next.core.dao;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.saidgadjiev.orm.next.core.db.H2DatabaseType;
import ru.saidgadjiev.orm.next.core.field.DBField;
import ru.saidgadjiev.orm.next.core.support.DataSourceConnectionSource;

public class TransactionImplTest {

    private WrappedConnectionSource connectionSource;

    @Before
    public void setUp() throws Exception {
        JdbcDataSource dataSource = new JdbcDataSource();

        dataSource.setURL("jdbc:h2:mem:h2testdb");
        connectionSource = new WrappedConnectionSource(new DataSourceConnectionSource(dataSource, new H2DatabaseType()));
    }

    @After
    public void after() throws Exception {
        connectionSource.close();
        connectionSource = null;
    }


    @Test
    public void commitTrans() throws Exception {
        Session dao = createDao(TestClazz.class, true);
        dao.createTable(TestClazz.class, true);
        TransactionImpl transaction = dao.transaction();
        TestClazz testClazz = new TestClazz();

        testClazz.name = "Test";
        transaction.begin();
        transaction.create(testClazz);
        transaction.rollback();

        Assert.assertEquals(0, dao.queryForAll(TestClazz.class).size());

        transaction.begin();
        transaction.create(testClazz);
        transaction.commit();

        Assert.assertEquals(1, dao.queryForAll(TestClazz.class).size());
    }

    protected <T> Session createDao(Class<T> clazz, boolean createTable) throws Exception {
        Session dao = new BaseSessionManagerImpl(connectionSource).getSession();

        if (createTable) {
            dao.createTable(TestClazz.class, true);
        }

        return dao;
    }

    private static class TestClazz {
        @DBField(id = true, generated = true)
        private int id;

        @DBField
        private String name;
    }
}