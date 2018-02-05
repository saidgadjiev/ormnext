package ru.saidgadjiev.orm.next.core.stress;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.TableUtils;
import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import ru.saidgadjiev.orm.next.core.dao.Dao;
import ru.saidgadjiev.orm.next.core.dao.DaoManager;
import ru.saidgadjiev.orm.next.core.db.H2DatabaseType;
import ru.saidgadjiev.orm.next.core.field.DBField;
import ru.saidgadjiev.orm.next.core.field.DataType;
import ru.saidgadjiev.orm.next.core.stament_executor.CachedStatementExecutorTest;
import ru.saidgadjiev.orm.next.core.support.DataSourceConnectionSource;

import javax.persistence.*;
import javax.sql.DataSource;
import javax.swing.plaf.TableUI;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
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

    private static void ormNext() throws Exception {
        JdbcDataSource dataSource = new JdbcDataSource();

        dataSource.setURL("jdbc:h2:mem:h2testdb;DB_CLOSE_DELAY=-1");
        Dao<TestClass, Integer> dao = DaoManager.createDAO(new DataSourceConnectionSource(dataSource, new H2DatabaseType()), TestClass.class);
        dao.createTable(false);
        List<TestClass> sourceClasses = new ArrayList<>();

        for (int i = 0; i < 10000; ++i) {
            TestClass testClass = createTestClazz(TestClass.class,0, "Said");

            sourceClasses.add(testClass);
        }
        long start = System.currentTimeMillis();

        for (TestClass testClass: sourceClasses) {
            dao.create(testClass);
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    private static void ormHibernate() throws Exception {
        Configuration configuration = new Configuration();

        configuration.addAnnotatedClass(TestClassHibernate.class);
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem:h2testdb;DB_CLOSE_DELAY=-1");
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create");

        SessionFactory sessionFactory = createSessionFactory(configuration);
        List<TestClassHibernate> sourceClasses = new ArrayList<>();

        for (int i = 0; i < 10000; ++i) {
            TestClassHibernate testClass = createTestClazz(TestClassHibernate.class,0, "Said");

            sourceClasses.add(testClass);
        }
        long start = System.currentTimeMillis();
        Session session = sessionFactory.openSession();

        for (TestClassHibernate testClass: sourceClasses) {
            session.save(testClass);
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
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

    }

    private static class TestClass {
        @DBField(id = true, dataType = DataType.INTEGER, generated = true)
        private int id;

        @DBField(dataType = DataType.STRING)
        private String name;
    }

    @Entity
    private static class TestClassHibernate {

        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.AUTO)
        private int id;

        @Column(name = "name")
        private String name;

        public TestClassHibernate() {
        }
    }

}
