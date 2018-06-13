package ru.saidgadjiev.ormnext.core.example.foreigncollection;

import org.junit.Assert;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.dao.SessionManager;
import ru.saidgadjiev.ormnext.core.util.TestUtils;

import java.sql.SQLException;

public class ForeignCollectionMain {

    public static void main(String[] args) throws SQLException {
        try (SessionManager sessionManager = TestUtils.h2SessionManager(Test.class, TestForeignCollection.class)) {
            try (Session session = sessionManager.createSession()) {
                session.createTables(true, new Class[] {Test.class, TestForeignCollection.class});
                Test test = new Test();

                session.create(test);
                TestForeignCollection testForeign = new TestForeignCollection();

                testForeign.setName("test");
                testForeign.setTest(test);
                session.create(testForeign);

                test.addTestForeign(testForeign);

                Test result = session.queryForId(Test.class, 1);

                Assert.assertEquals(result, test);
                Assert.assertEquals(test.getTestForeigns(), result.getTestForeigns());
            }
        }
    }
}
