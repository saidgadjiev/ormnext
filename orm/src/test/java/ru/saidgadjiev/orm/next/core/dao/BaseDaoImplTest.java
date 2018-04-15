package ru.saidgadjiev.orm.next.core.dao;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.saidgadjiev.orm.next.core.db.H2DatabaseType;
import ru.saidgadjiev.orm.next.core.field.DatabaseColumn;
import ru.saidgadjiev.orm.next.core.field.DataType;
import ru.saidgadjiev.orm.next.core.field.ForeignCollectionField;
import ru.saidgadjiev.orm.next.core.field.ForeignColumn;
import ru.saidgadjiev.orm.next.core.support.DataSourceConnectionSource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by said on 28.01.2018.
 */
public class BaseDaoImplTest {

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
    public void createNoColumn() throws Exception {
        Session dao = createDao(TestNoColumn.class, true);
        TestNoColumn testNoColumn = new TestNoColumn();

        Assert.assertEquals(1, dao.create(testNoColumn));
        TestNoColumn result = dao.queryForId(TestNoColumn.class, testNoColumn.id);

        Assert.assertEquals(1, result.id);
    }

    @Test
    public void createAndQueryForIdForeignCollection() throws Exception {
        Session daoTestForeignClass = createDao(TestForeignCollectionClass.class, true);
        Session daoTestForeign = createDao(TestForeignClass.class, true);
        TestForeignCollectionClass employee = new TestForeignCollectionClass();

        Assert.assertEquals(1, daoTestForeignClass.create(employee));
        TestForeignClass testForeignClass1 = new TestForeignClass();

        testForeignClass1.foreign = employee;
        Assert.assertEquals(1, daoTestForeign.create(testForeignClass1));
        TestForeignCollectionClass result = daoTestForeignClass.queryForId(TestForeignCollectionClass.class, employee.id);

        Assert.assertEquals(1, result.id);
        Assert.assertEquals(1, result.foreignClasses.size());
    }

    @Test
    public void createAndQueryForId() throws Exception {
        Session dao = createDao(TestClazz.class, true);
        TestClazz employee = new TestClazz();

        employee.name = "Said";
        Assert.assertEquals(1, dao.create(employee));
        TestClazz result = dao.queryForId(TestClazz.class, employee.id);

        Assert.assertEquals(1, result.id);
        Assert.assertEquals(result.name, "Said");
    }

    @Test
    public void queryForAll() throws Exception {
        Session dao = createDao(TestClazz.class, true);
        TestClazz employee = new TestClazz();

        employee.name = "Said";
        Assert.assertEquals(1, dao.create(employee));
        List<TestClazz> result = dao.queryForAll(TestClazz.class);

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(result.get(0).name, "Said");
        Assert.assertEquals(result.get(0).id, 1);
    }

    @Test
    public void update() throws Exception {
        Session dao = createDao(TestClazz.class, true);
        TestClazz employee = new TestClazz();

        employee.name = "Said";
        Assert.assertEquals(1, dao.create(employee));
        employee.name = "TestChanged";
        int count = dao.update(employee);
        TestClazz resultEmployee = dao.queryForId(TestClazz.class, employee.id);

        Assert.assertEquals(1, count);
        Assert.assertEquals("TestChanged", resultEmployee.name);
    }

    @Test
    public void delete() throws Exception {
        Session dao = createDao(TestClazz.class, true);
        TestClazz employee = new TestClazz();

        employee.name = "Said";
        Assert.assertEquals(1, dao.create(employee));
        int count = dao.delete(employee);
        TestClazz checkEmployee = dao.queryForId(TestClazz.class, employee.id);

        Assert.assertEquals(1, count);
        Assert.assertNull(checkEmployee);
    }

    @Test
    public void deleteById() throws Exception {
        Session dao = createDao(TestClazz.class, true);
        TestClazz employee = new TestClazz();

        employee.name = "Said";
        Assert.assertEquals(1, dao.create(employee));
        int count = dao.deleteById(TestClazz.class, employee.id);
        TestClazz checkEmployee = dao.queryForId(TestClazz.class, employee.id);

        Assert.assertEquals(1, count);
        Assert.assertNull(checkEmployee);
    }

    @Test
    public void countOff() throws Exception {
        Session dao = createDao(TestClazz.class, true);
        TestClazz employee = new TestClazz();

        Assert.assertEquals(1, dao.create(employee));
        Assert.assertEquals(1, dao.countOff(TestClazz.class));
    }

    @Test
    public void createTable() throws Exception {
        Session dao = createDao(TestCreateTable.class, false);

        Assert.assertTrue(dao.createTable(TestCreateTable.class, true));
    }

    private static class TestForeignCollectionClass {
        @DatabaseColumn(id = true, generated = true)
        private int id;

        @ForeignCollectionField(foreignFieldName = "foreign")
        private List<TestForeignClass> foreignClasses = new ArrayList<>();

    }

    private static class TestForeignClass {
        @DatabaseColumn(id = true, generated = true)
        private int id;

        @ForeignColumn
        private TestForeignCollectionClass foreign;
    }

    private static class TestClazz {
        @DatabaseColumn(id = true, generated = true)
        private int id;

        @DatabaseColumn
        private String name;

        @DatabaseColumn
        private Date date;
    }

    private static class TestForeign {
        @DatabaseColumn(id = true, generated = true)
        private int id;

        @DatabaseColumn
        private String name;

        @ForeignCollectionField
        private List<TestClazz> testClazz = new ArrayList<>();
    }

    private static class TestCreateTable {
        @DatabaseColumn(id = true, dataType = DataType.INTEGER, generated = true)
        private int id;

        @DatabaseColumn(dataType = DataType.STRING)
        private String name;


        @DatabaseColumn(dataType = DataType.DATE, notNull = true, defaultValue = "03:02:2018", format = "dd:MM:yyyy")
        private Date date;

        @DatabaseColumn(dataType = DataType.BOOLEAN, defaultValue = "true")
        private Boolean bool;

        @DatabaseColumn(defaultValue = "1.023", dataType = DataType.DOUBLE)
        private Double doub;

        @DatabaseColumn(dataType = DataType.FLOAT)
        private Float aFloat;

        @DatabaseColumn(dataType = DataType.LONG)
        private Long lon;
    }

    public static class TestNoColumn {
        @DatabaseColumn(id = true, generated = true)
        private int id;
    }

    protected <T> Session createDao(Class<T> clazz, boolean createTable) throws Exception {
        Session dao = new BaseSessionManagerImpl(connectionSource).getCurrentSession();

        if (createTable) {
            dao.createTable(clazz, true);
        }

        return dao;
    }

}