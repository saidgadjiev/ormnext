package ru.saidgadjiev.ormnext.core;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import ru.saidgadjiev.ormnext.core.connection.source.DataSourceConnectionSource;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.dao.SessionManager;
import ru.saidgadjiev.ormnext.core.dao.SessionManagerBuilder;
import ru.saidgadjiev.ormnext.core.dialect.H2Dialect;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.ormnext.core.model.*;

import java.sql.SQLException;

public class BaseCoreTest {

    protected static SessionManager sessionManager;

    @BeforeClass
    public static void setUpClass() {
        System.setProperty(LoggerFactory.LOG_ENABLED_PROPERTY, "true");
        JdbcDataSource dataSource = new JdbcDataSource();

        dataSource.setURL("jdbc:h2:mem:h2testdatabase");
        sessionManager = new SessionManagerBuilder()
                .entities(
                        TestEntity.class,
                        ForeignTestEntity.class,
                        ForeignCollectionTestEntity.class,
                        WithDefaultTestEntity.class,
                        ForeignAutoCreateForeignColumnTestEntity.class,
                        ForeignAutoCreateForeignCollectionColumnTestEntity.class
                ).databaseType(new H2Dialect())
                .connectionSource(new DataSourceConnectionSource(dataSource))
                .build();
    }

    @AfterClass
    public static void tearDownClass() throws SQLException {
        if (sessionManager != null) {
            sessionManager.close();
        }
    }

    protected Session createSessionAndCreateTables(Class<?> ... classes) throws SQLException {
        Session session = sessionManager.createSession();

        if (classes.length == 0) {
            session.createTables(new Class[]{
                    ForeignCollectionTestEntity.class,
                    ForeignAutoCreateForeignCollectionColumnTestEntity.class,
                    TestEntity.class,
                    ForeignTestEntity.class,
                    WithDefaultTestEntity.class,
                    ForeignAutoCreateForeignColumnTestEntity.class
            }, true);
        } else {
            for (Class<?> clazz: classes) {
                session.createTable(clazz, true);
            }
        }

        return session;
    }
}
