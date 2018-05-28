package ru.saidgadjiev.ormnext.core.dao;

import org.junit.Test;
import ru.saidgadjiev.ormnext.core.BaseCoreTest;
import ru.saidgadjiev.ormnext.core.model.ForeignSimpleEntity;
import ru.saidgadjiev.ormnext.core.model.SimpleEntity;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class SessionImplTest extends BaseCoreTest {

    @Test
    public void queryForId() throws SQLException {
        try (Session session = sessionManager.createSession()) {
            session.createTable(SimpleEntity.class, true);
            session.createTable(ForeignSimpleEntity.class, true);
            SimpleEntity entity = new SimpleEntity();

            session.create(entity);
            ForeignSimpleEntity foreignSimpleEntity = new ForeignSimpleEntity();

            foreignSimpleEntity.setEntity(entity);
            session.create(foreignSimpleEntity);

            ForeignSimpleEntity result = session.queryForId(ForeignSimpleEntity.class, 1);
        }
    }
}