package ru.saidgadjiev.ormnext.core;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import ru.saidgadjiev.ormnext.core.dao.*;
import ru.saidgadjiev.ormnext.core.util.TestUtils;

import java.sql.SQLException;

public class BaseCoreTest {

    private static SessionManager sessionManager;

    @BeforeClass
    public static void setUpClass() throws SQLException {
        sessionManager = TestUtils.h2SessionManager(
                TestQueryForId.A.class,
                TestQueryForId.AForeignCollection.class,
                TestQueryForAll.A.class,
                TestQueryForAll.AForeignCollection.class,
                TestUpdate.A.class,
                TestDelete.A.class,
                TestDeleteById.A.class,
                TestSelfJoin.A.class,
                TestSelfJoinLazy.A.class,
                TestListWithSelectColumns.A.class,
                TestListWithSelectColumns.AForeignCollection.class,
                TestList.A.class,
                TestQueryForLong.A.class,
                TestDeleteStatement.A.class,
                TestUpdateStatement.A.class,
                TestUniqueResult.A.class,
                TestExist.A.class,
                TestGeneratedId.A.class,
                TestCreateWithDefaultIfNull.A.class,
                TestCreateOrUpdateCreate.A.class,
                TestCreateOrUpdateUpdate.A.class,
                TestRefresh.A.class,
                TestRefresh.AForeignCollection.class,
                TestLazy.AForeignCollection.class,
                TestLazy.A.class,
                TestForeignAutoCreate.A.class,
                TestForeignAutoCreate.AForeignCollection.class,
                TestForeignAutoCreate.AForeign.class,
                TestForeignReference.A.class,
                TestForeignReference.AForeignCollection.class
        );
    }

    @AfterClass
    public static void tearDownClass() throws SQLException {
        if (sessionManager != null) {
            sessionManager.close();
        }
    }

    protected Session createSessionAndCreateTables(Class<?>... classes) throws SQLException {
        Session session = sessionManager.createSession();


        for (Class<?> clazz : classes) {
            session.createTable(clazz, true);
        }

        return session;
    }
}
