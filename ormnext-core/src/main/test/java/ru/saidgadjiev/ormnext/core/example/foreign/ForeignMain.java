package ru.saidgadjiev.ormnext.core.example.foreign;

import org.junit.Assert;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.dao.SessionManager;
import ru.saidgadjiev.ormnext.core.util.TestUtils;

import java.sql.SQLException;

public class ForeignMain {

    public static void main(String[] args) throws SQLException {
        try (SessionManager sessionManager = TestUtils.h2SessionManager(Test.class, TestForeign.class)) {
            try (Session session = sessionManager.createSession()) {
                session.createTables(new Class[] {TestForeign.class, Test.class}, true);
                TestForeign testForeign = new TestForeign();

                testForeign.setName("test");
                session.create(testForeign);
                Test test = new Test();

                test.setTestForeign(testForeign);
                session.create(test);
                Assert.assertEquals(session.queryForId(Test.class, 1), test);
            }
        }
    }
}
