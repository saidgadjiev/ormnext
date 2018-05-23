package ru.saidgadjiev.ormnext.core.criteria.impl;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.saidgadjiev.ormnext.core.connection_source.DataSourceConnectionSource;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.dao.SessionManager;
import ru.saidgadjiev.ormnext.core.dao.SessionManagerBuilder;
import ru.saidgadjiev.ormnext.core.database_type.H2DatabaseType;
import ru.saidgadjiev.ormnext.core.model.TestEntity;

import java.sql.SQLException;
import java.util.List;

public class CriteriaQueryTest {

    private static SessionManager sessionManager;

    @BeforeClass
    public static void setUp() throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();

        dataSource.setURL("jdbc:h2:mem:h2testdatabase;DB_CLOSE_DELAY=-1");
        sessionManager = new SessionManagerBuilder()
                .entities(TestEntity.class)
                .databaseType(new H2DatabaseType())
                .connectionSource(new DataSourceConnectionSource(dataSource))
                .build();
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        sessionManager.close();
    }

    @Test
    public void testSimpleSelect() throws SQLException {
        Session session = sessionManager.createSession();

        session.createTable(TestEntity.class, false);
        CriteriaQuery<TestEntity> entityCriteriaQuery = new CriteriaQuery<>(TestEntity.class)
                .where(new Criteria()
                        .add(Restrictions.eq("id", 1))
                );
        TestEntity testEntity = new TestEntity();

        session.create(testEntity);
        List<TestEntity> list = session.list(entityCriteriaQuery);

        Assert.assertEquals(1, list.size());
        Assert.assertEquals(testEntity, list.get(0));
    }

    @Test
    public void testSimpleSelectLong() throws SQLException {
        Session session = sessionManager.createSession();

        session.createTable(TestEntity.class, false);
        CriteriaQuery<TestEntity> entityCriteriaQuery = new CriteriaQuery<>(TestEntity.class).countOff();
        TestEntity testEntity = new TestEntity();

        session.create(testEntity);
        Assert.assertEquals(1, session.queryForLong(entityCriteriaQuery));
    }
}