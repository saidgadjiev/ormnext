package ru.said.orm.next.core.dao;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.said.orm.next.core.db.H2DatabaseType;
import ru.said.orm.next.core.field.DBField;
import ru.said.orm.next.core.support.DataSourceConnectionSource;
import ru.said.orm.next.core.table.TableInfo;
import ru.said.orm.next.core.table.utils.TableUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by said on 28.01.2018.
 */
public class BaseDaoImplTest {

    private WrappedConnectionSource connectionSource;

    @Before
    public void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();

        dataSource.setURL("jdbc:h2:mem:h2testdb");
        connectionSource = new WrappedConnectionSource(new DataSourceConnectionSource(dataSource, new H2DatabaseType()));
        DaoManager.clearCache();
    }

    @After
    public void after() throws Exception {
        connectionSource.close();
        connectionSource = null;
    }

    @Test
    public void createAndQueryForId() throws Exception {
        Dao<TestClazz, Integer> dao = createDao(TestClazz.class, true);
        TestClazz employee = new TestClazz();

        employee.name = "Said";
        Assert.assertEquals(1, dao.create(employee));
        TestClazz result = dao.queryForId(employee.id);

        Assert.assertEquals(1, result.id);
        Assert.assertEquals(result.name, "Said");
    }

    @Test
    public void queryForAll() throws Exception {
        Dao<TestClazz, Integer> dao = createDao(TestClazz.class, true);
        TestClazz employee = new TestClazz();

        employee.name = "Said";
        Assert.assertEquals(1, dao.create(employee));
        List<TestClazz> result = dao.queryForAll();

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(result.get(0).name, "Said");
        Assert.assertEquals(result.get(0).id, 1);
    }

    @Test
    public void update() throws Exception {
        Dao<TestClazz, Integer> dao = createDao(TestClazz.class, true);
        TestClazz employee = new TestClazz();

        employee.name = "Said";
        Assert.assertEquals(1, dao.create(employee));
        employee.name = "TestChanged";
        int count = dao.update(employee);
        TestClazz resultEmployee = dao.queryForId(employee.id);

        Assert.assertEquals(1, count);
        Assert.assertEquals("TestChanged", resultEmployee.name);
    }

    @Test
    public void delete() throws Exception {
        Dao<TestClazz, Integer> dao = createDao(TestClazz.class, true);
        TestClazz employee = new TestClazz();

        employee.name = "Said";
        Assert.assertEquals(1, dao.create(employee));
        int count = dao.delete(employee);
        TestClazz checkEmployee = dao.queryForId(employee.id);

        Assert.assertEquals(1, count);
        Assert.assertNull(checkEmployee);
    }

    @Test
    public void deleteById() throws Exception {
        Dao<TestClazz, Integer> dao = createDao(TestClazz.class, true);
        TestClazz employee = new TestClazz();

        employee.name = "Said";
        Assert.assertEquals(1, dao.create(employee));
        int count = dao.deleteById(employee.id);
        TestClazz checkEmployee = dao.queryForId(employee.id);

        Assert.assertEquals(1, count);
        Assert.assertNull(checkEmployee);
    }

    @Test
    public void countOff() throws Exception {
        Dao<TestClazz, Integer> dao = createDao(TestClazz.class, true);
        TestClazz employee = new TestClazz();

        Assert.assertEquals(1, dao.create(employee));
        Assert.assertEquals(1, dao.countOff());
    }


    private static class TestClazz {
        @DBField(id = true, generated = true)
        private int id;

        @DBField
        private String name;
    }

    protected <T, ID> Dao<T, ID> createDao(Class<T> clazz, boolean createTable) throws Exception {
        Dao<T, ID> dao = DaoManager.createDAO(connectionSource, clazz);

        if (createTable) {
            dao.createTable(true);
        }

        return dao;
    }

}