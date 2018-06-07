package ru.saidgadjiev.ormnext.core.dao;

import org.junit.Assert;
import org.junit.Test;
import ru.saidgadjiev.ormnext.core.BaseCoreTest;
import ru.saidgadjiev.ormnext.core.model.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SessionImplTest extends BaseCoreTest {

    @Test
    public void testQueryForId() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            TestEntity entity = new TestEntity();

            session.create(entity);
            TestEntity resultEntity = session.queryForId(TestEntity.class, 1);

            Assert.assertEquals(entity, resultEntity);
        }
    }

    @Test
    public void testGeneratedIdSetToCreatedObject() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            TestEntity entity1 = new TestEntity();

            session.create(entity1);
            Assert.assertEquals(1, entity1.getId());
            TestEntity entity2 = new TestEntity();

            session.create(entity2);
            Assert.assertEquals(2, entity2.getId());
        }
    }

    @Test
    public void testQueryForAll() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            List<TestEntity> entities = new ArrayList<>();

            entities.add(new TestEntity());
            entities.add(new TestEntity());
            entities.add(new TestEntity());

            session.create(entities.toArray());
            Assert.assertEquals(session.queryForAll(TestEntity.class), entities);
        }
    }

    @Test
    public void testUpdate() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            TestEntity entity = new TestEntity();

            entity.setDesc("Test");
            session.create(entity);
            entity.setDesc("TestDesc");
            session.update(entity);

            TestEntity updatedEntity = session.queryForId(TestEntity.class, entity.getId());

            Assert.assertEquals(updatedEntity.getDesc(), "TestDesc");
        }
    }

    @Test
    public void testDelete() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            TestEntity entity = new TestEntity();

            Assert.assertEquals(1, session.create(entity));
            Assert.assertEquals(entity, session.queryForId(TestEntity.class, 1));

            Assert.assertEquals(1, session.delete(entity));
            Assert.assertNull(session.queryForId(TestEntity.class, 1));
        }
    }

    @Test
    public void testDeleteById() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            TestEntity entity = new TestEntity();

            Assert.assertEquals(1, session.create(entity));
            Assert.assertEquals(entity, session.queryForId(TestEntity.class, 1));

            Assert.assertEquals(1, session.deleteById(TestEntity.class, 1));
            Assert.assertNull(session.queryForId(TestEntity.class, 1));
        }
    }

    @Test
    public void testQueryForIdForeign() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            TestEntity entity = new TestEntity();

            entity.setDesc("Test");
            session.create(entity);
            ForeignTestEntity foreignTestEntity = new ForeignTestEntity();

            foreignTestEntity.setEntity(entity);
            session.create(foreignTestEntity);

            Assert.assertEquals(foreignTestEntity, session.queryForId(ForeignTestEntity.class, 1));
        }
    }

    @Test
    public void testQueryForIdForeignCollection() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            ForeignCollectionTestEntity foreignCollectionTestEntity = new ForeignCollectionTestEntity();

            session.create(foreignCollectionTestEntity);
            TestEntity entity = new TestEntity();

            entity.setDesc("Test");
            entity.setForeignCollectionTestEntity(foreignCollectionTestEntity);
            session.create(entity);

            ForeignCollectionTestEntity resultEntity = session.queryForId(ForeignCollectionTestEntity.class, 1);

            Assert.assertEquals(
                    foreignCollectionTestEntity,
                    resultEntity
            );
            Assert.assertEquals(resultEntity.getEntities().get(0), entity);
        }
    }

    @Test
    public void testQueryForAllForeign() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            TestEntity entity = new TestEntity();

            entity.setDesc("Test");
            session.create(entity);
            ForeignTestEntity foreignTestEntity = new ForeignTestEntity();

            foreignTestEntity.setEntity(entity);
            session.create(foreignTestEntity);

            List<ForeignTestEntity> result = session.queryForAll(ForeignTestEntity.class);

            Assert.assertEquals(1, result.size());
            Assert.assertEquals(foreignTestEntity, result.get(0));
        }
    }

    @Test
    public void testQueryForAllForeignCollection() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            ForeignCollectionTestEntity foreignCollectionTestEntity = new ForeignCollectionTestEntity();

            session.create(foreignCollectionTestEntity);
            TestEntity entity = new TestEntity();

            entity.setDesc("Test");
            entity.setForeignCollectionTestEntity(foreignCollectionTestEntity);
            session.create(entity);

            List<ForeignCollectionTestEntity> resultEntity = session.queryForAll(ForeignCollectionTestEntity.class);

            Assert.assertEquals(
                    foreignCollectionTestEntity,
                    resultEntity.get(0)
            );
            Assert.assertEquals(resultEntity.get(0).getEntities().get(0), entity);
        }
    }

    @Test
    public void testCreateWithDefaultIfNull() throws SQLException {
        try (Session session = createSessionAndCreateTables(WithDefaultTestEntity.class)) {
            WithDefaultTestEntity withDefaultTestEntity = new WithDefaultTestEntity();

            session.create(withDefaultTestEntity);
            WithDefaultTestEntity result = session.queryForId(WithDefaultTestEntity.class, 1);

            Assert.assertEquals(1, result.getId());
            Assert.assertEquals("test", result.getName());
        }
    }

    @Test
    public void testForeignAutoCreateForForeignColumn() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            ForeignAutoCreateForeignColumnTestEntity testForeignAutoCreateEntity =
                    new ForeignAutoCreateForeignColumnTestEntity();
            TestEntity testEntity = new TestEntity();

            testEntity.setDesc("Test");
            testForeignAutoCreateEntity.setTestEntity(testEntity);
            session.create(testForeignAutoCreateEntity);
            ForeignAutoCreateForeignColumnTestEntity result = session.queryForId(
                    ForeignAutoCreateForeignColumnTestEntity.class,
                    1
            );

            Assert.assertEquals(result.getTestEntity(), testEntity);
        }
    }

    @Test
    public void testForeignAutoCreateForForeignCollectionColumn() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            ForeignAutoCreateForeignCollectionColumnTestEntity testForeignAutoCreateEntity =
                    new ForeignAutoCreateForeignCollectionColumnTestEntity();
            TestEntity testEntity = new TestEntity();

            testEntity.setDesc("Test");
            testForeignAutoCreateEntity.getEntities().add(testEntity);
            session.create(testForeignAutoCreateEntity);
            ForeignAutoCreateForeignCollectionColumnTestEntity result = session.queryForId(
                    ForeignAutoCreateForeignCollectionColumnTestEntity.class,
                    1
            );

            Assert.assertEquals(result.getEntities().get(0), testEntity);
        }
    }

    @Test
    public void testRefreshForeign() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            ForeignTestEntity foreignTestEntity = new ForeignTestEntity();

            TestEntity testEntity = new TestEntity();

            session.create(testEntity);
            foreignTestEntity.setEntity(testEntity);
            session.create(foreignTestEntity);
            ForeignTestEntity resultBefore = session.queryForId(ForeignTestEntity.class, 1);

            Assert.assertEquals(foreignTestEntity, resultBefore);

            testEntity.setDesc("Test");
            session.update(testEntity);
            session.refresh(resultBefore);
            Assert.assertEquals(resultBefore, foreignTestEntity);
        }
    }

    @Test
    public void testRefreshForeignCollection() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            ForeignCollectionTestEntity foreignTestEntity = new ForeignCollectionTestEntity();

            session.create(foreignTestEntity);
            TestEntity testEntity = new TestEntity();

            testEntity.setForeignCollectionTestEntity(foreignTestEntity);
            foreignTestEntity.getEntities().add(testEntity);
            session.create(testEntity);
            ForeignCollectionTestEntity resultBefore = session.queryForId(ForeignCollectionTestEntity.class, 1);

            Assert.assertEquals(foreignTestEntity.getEntities(), resultBefore.getEntities());

            testEntity.setDesc("Test");
            session.update(testEntity);
            session.refresh(resultBefore);
            Assert.assertEquals(resultBefore.getEntities(), foreignTestEntity.getEntities());
        }
    }

    @Test
    public void testLazyCollection() throws SQLException {
        try (Session session = createSessionAndCreateTables(TestLazyCollection.class, TestLazy.class)) {
            TestLazyCollection testLazyCollection = new TestLazyCollection();

            session.create(testLazyCollection);
            TestLazy testLazy = new TestLazy();

            testLazy.setDesc("TestLazy");
            testLazy.setTestLazyCollection(testLazyCollection);

            session.create(testLazy);
            testLazyCollection.getEntities().add(testLazy);
            TestLazyCollection result = session.queryForId(TestLazyCollection.class, 1);
            Assert.assertEquals(result.getEntities(), testLazyCollection.getEntities());
        }
    }

    @Test
    public void testLazyForeign() throws SQLException {
        try (Session session = createSessionAndCreateTables(TestLazyCollection.class, TestLazyForeign.class)) {
            TestLazyCollection testLazyCollection = new TestLazyCollection();

            session.create(testLazyCollection);
            TestLazyForeign testLazy = new TestLazyForeign();

            testLazy.setDesc("TestLazy");
            testLazy.setTestLazyCollection(testLazyCollection);

            session.create(testLazy);
            TestLazyForeign result = session.queryForId(TestLazyForeign.class, 1);
            Assert.assertEquals(result, testLazy);
        }
    }
}