package ru.saidgadjiev.ormnext.core.example.simple;

import org.junit.Assert;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.dao.SessionManager;
import ru.saidgadjiev.ormnext.core.util.TestUtils;

import java.sql.SQLException;

public class SimpleMain {

    public static void main(String[] args) throws SQLException {
        try (SessionManager sessionManager = TestUtils.h2SessionManager(Test.class)) {
            try (Session session = sessionManager.createSession()) {
                session.createTable(true, Test.class);
                Test test = new Test();

                session.create(test);
                Assert.assertEquals(session.queryForId(Test.class, 1), test);
            }
        }
    }
}
