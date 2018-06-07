package ru.saidgadjiev.ormnext.core.example.foreign;

import org.junit.Assert;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.dao.SessionManager;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.ormnext.core.util.TestUtils;

import java.sql.SQLException;

public class ForeignMain {

    public static void main(String[] args) throws SQLException {
        System.setProperty(LoggerFactory.LOG_ENABLED_PROPERTY, "true");
        try (SessionManager sessionManager = TestUtils.h2SessionManager(TestForeign.class, Test.class)) {
            try (Session session = sessionManager.createSession()) {
                session.createTables(new Class[] {TestForeign.class, Test.class}, true);
                TestForeign testForeign = new TestForeign();

                testForeign.setName("TestForeign");
                session.create(testForeign);
                Test test = new Test();

                test.setTestForeign(testForeign);
                test.setName("Test");
                session.create(test);
                Assert.assertEquals(session.queryForId(Test.class, 1), test);
            }
        }
    }
}
