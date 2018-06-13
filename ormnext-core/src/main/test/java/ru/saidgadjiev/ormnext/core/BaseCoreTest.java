package ru.saidgadjiev.ormnext.core;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.dao.SessionManager;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.ormnext.core.model.*;
import ru.saidgadjiev.ormnext.core.util.TestUtils;

import java.sql.SQLException;

public class BaseCoreTest {

    protected static SessionManager sessionManager;

    @BeforeClass
    public static void setUpClass() throws SQLException {
        System.setProperty(LoggerFactory.LOG_ENABLED_PROPERTY, "true");
        sessionManager = TestUtils.h2SessionManager(
                TestEntity.class,
                ForeignTestEntity.class,
                ForeignCollectionTestEntity.class,
                WithDefaultTestEntity.class,
                ForeignAutoCreateForeignColumnTestEntity.class,
                ForeignAutoCreateForeignCollectionColumnTestEntity.class,
                TestLazyCollection.class,
                TestLazy.class,
                TestLazyForeign.class,
                SelfJoinTestEntity.class,
                UniqueFieldTestEntity.class,
                TableUniqueFieldTestEntity.class,
                ForeignFieldReferenceTestEntity.class
        );
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
            session.createTables(true,
                    ForeignCollectionTestEntity.class,
                    ForeignAutoCreateForeignCollectionColumnTestEntity.class,
                    TestEntity.class,
                    ForeignTestEntity.class,
                    WithDefaultTestEntity.class,
                    ForeignAutoCreateForeignColumnTestEntity.class
            );
        } else {
            for (Class<?> clazz: classes) {
                session.createTable(true, clazz);
            }
        }

        return session;
    }
}
