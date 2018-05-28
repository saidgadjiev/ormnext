package ru.saidgadjiev.ormnext.core.query.criteria.impl;

import org.junit.Assert;
import org.junit.Test;
import ru.saidgadjiev.ormnext.core.BaseCoreTest;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.model.SimpleEntity;

import java.sql.SQLException;
import java.util.List;

public class SelectStatementTest extends BaseCoreTest {

    @Test
    public void testSimpleSelect() throws SQLException {
        try (Session session = sessionManager.createSession()) {

            session.createTable(SimpleEntity.class, true);
            SelectStatement<SimpleEntity> entitySelectStatement = new SelectStatement<>(SimpleEntity.class)
                    .where(new Criteria()
                            .add(Restrictions.eq("id", 1))
                    );
            SimpleEntity simpleEntity = new SimpleEntity();

            session.create(simpleEntity);
            List<SimpleEntity> list = session.list(entitySelectStatement);

            Assert.assertEquals(1, list.size());
            Assert.assertEquals(simpleEntity, list.get(0));
        }
    }

    @Test
    public void testSimpleSelectLong() throws SQLException {
        try (Session session = sessionManager.createSession()) {

            session.createTable(SimpleEntity.class, true);
            SelectStatement<SimpleEntity> entitySelectStatement = new SelectStatement<>(SimpleEntity.class).countOff();
            SimpleEntity simpleEntity = new SimpleEntity();

            session.create(simpleEntity);
            Assert.assertEquals(1, session.queryForLong(entitySelectStatement));
        }
    }
}