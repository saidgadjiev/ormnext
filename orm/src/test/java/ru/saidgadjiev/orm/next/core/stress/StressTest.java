package ru.saidgadjiev.orm.next.core.stress;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.TableUtils;
import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import ru.saidgadjiev.orm.next.core.StressUtils;
import ru.saidgadjiev.orm.next.core.cache.LRUObjectCache;
import ru.saidgadjiev.orm.next.core.dao.BaseSessionManagerImpl;
import ru.saidgadjiev.orm.next.core.db.H2DatabaseType;
import ru.saidgadjiev.orm.next.core.field.DBField;
import ru.saidgadjiev.orm.next.core.field.DataType;
import ru.saidgadjiev.orm.next.core.field.Getter;
import ru.saidgadjiev.orm.next.core.field.Setter;
import ru.saidgadjiev.orm.next.core.support.PolledConnectionSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 04.02.2018.
 */
public class StressTest {

    public static void main(String[] args) throws Exception {
        ormHibernate();
    }

    private static void ormLite() throws Exception {
        JdbcDataSource dataSource = new JdbcDataSource();

        dataSource.setURL("jdbc:h2:mem:h2testdb;DB_CLOSE_DELAY=-1");
        com.j256.ormlite.dao.Dao<TestClassOrmLite, Integer> dao = com.j256.ormlite.dao.DaoManager.createDao(
                new com.j256.ormlite.jdbc.DataSourceConnectionSource(dataSource, new com.j256.ormlite.db.H2DatabaseType()), TestClassOrmLite.class);
        TableUtils.createTable(dao.getConnectionSource(), TestClassOrmLite.class);
        List<TestClassOrmLite> sourceClasses = new ArrayList<>();

        for (int i = 0; i < 10000; ++i) {
            TestClassOrmLite testClass = new TestClassOrmLite();

            sourceClasses.add(testClass);
        }
        long start = System.currentTimeMillis();

        for (TestClassOrmLite testClass: sourceClasses) {
            dao.create(testClass);
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    private static void ormNextCreate() throws Exception {
        JdbcDataSource dataSource = new JdbcDataSource();

        dataSource.setURL("jdbc:h2:mem:h2testdb;DB_CLOSE_DELAY=-1");
        BaseSessionManagerImpl sessionManager = new BaseSessionManagerImpl(new PolledConnectionSource(dataSource, new H2DatabaseType()));
        ru.saidgadjiev.orm.next.core.dao.Session session = sessionManager.getCurrentSession();
        session.createTable(TestClass.class, false);
        List<TestClass> sourceClasses = new ArrayList<>();

        for (int i = 0; i < 10000; ++i) {
            TestClass testClass = createTestClazz(TestClass.class,0, "Said");

            sourceClasses.add(testClass);
        }
        long start = System.currentTimeMillis();

        for (TestClass testClass: sourceClasses) {
            session.create(testClass);
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        sessionManager.getDataSource().close();
    }


    private static void ormNextQueryForId() throws Exception {
        JdbcDataSource dataSource = new JdbcDataSource();

        dataSource.setURL("jdbc:h2:mem:h2testdb;DB_CLOSE_DELAY=-1");
        BaseSessionManagerImpl sessionManager = new BaseSessionManagerImpl(new PolledConnectionSource(dataSource, new H2DatabaseType()));
        ru.saidgadjiev.orm.next.core.dao.Session session = sessionManager.getCurrentSession();
        session.createTable(TestClass.class, false);
        sessionManager.setObjectCache(new LRUObjectCache(16), TestClass.class);

        TestClass testClass = createTestClazz(TestClass.class,0, "Said");

        session.create(testClass);

        System.out.println(StressUtils.stress(() -> {
            session.queryForId(TestClass.class, testClass.id);

            return null;
        }, 10000));
        sessionManager.getDataSource().close();
    }

    private static void ormHibernate() throws Exception {
        Configuration configuration = new Configuration();

        configuration.addAnnotatedClass(TestForeign.class);
        configuration.addAnnotatedClass(TestOneToMany.class);
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        configuration.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/SpaceHockey");
        configuration.setProperty("hibernate.connection.username", "root");
        configuration.setProperty("hibernate.connection.password", "said1995");
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.hbmdl.auto", "update");

        SessionFactory sessionFactory = createSessionFactory(configuration);
        Session session = sessionFactory.openSession();
        TestOneToMany testOneToMany1 = session.get(TestOneToMany.class, 1);
        TestForeign testForeign = new TestForeign();

        testForeign.name = "test_test_test";
        testOneToMany1.getTestForeign().add(testForeign);
        session.save(testOneToMany1);

        session.close();
    }

    private static<T> T createTestClazz(Class<T> tClass, Object ... args) throws Exception {
        Constructor<T> constructor = tClass.getConstructor();
        constructor.setAccessible(true);
        T object = constructor.newInstance();
        Field[] fields = tClass.getDeclaredFields();

        for (int i = 0; i < args.length; ++i) {
            fields[i].setAccessible(true);
            fields[i].set(object, args[i]);
            fields[i].setAccessible(false);
        }

        return object;
    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();

        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();

        return configuration.buildSessionFactory(serviceRegistry);
    }

    private static class TestClassOrmLite {
        @DatabaseField(dataType = com.j256.ormlite.field.DataType.INTEGER, generatedId = true)
        private int id;

        @DatabaseField(dataType = com.j256.ormlite.field.DataType.STRING)
        private String name;

        @DatabaseField(foreign = true)
        private TestForeignClassOrmLite test;
    }


    private static class TestForeignClassOrmLite {
        @DatabaseField(dataType = com.j256.ormlite.field.DataType.INTEGER, generatedId = true)
        private int id;

        @DatabaseField(dataType = com.j256.ormlite.field.DataType.STRING)
        private String name;
    }

    public static class TestClass {
        @Getter(name = "getId")
        @Setter(name = "setId")
        @DBField(id = true, dataType = DataType.INTEGER, generated = true)
        private int id;

        @Getter(name = "getName")
        @Setter(name = "setName")
        @DBField(dataType = DataType.STRING)
        private String name;

        public TestClass() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
