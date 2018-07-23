package ru.saidgadjiev.ormnext.core.dao;

import org.junit.Assert;
import org.junit.Test;
import ru.saidgadjiev.ormnext.core.BaseCoreTest;
import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.model.*;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SessionImplTest extends BaseCoreTest {

    @Test
    public void testQueryForId() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            A entity = new A();

            session.create(entity);
            A resultEntity = session.queryForId(A.class, 1);

            Assert.assertEquals(entity, resultEntity);
        }
    }

    @Test
    public void testGeneratedIdSetToCreatedObject() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            A entity1 = new A();

            session.create(entity1);
            Assert.assertEquals(1, entity1.getId());
            A entity2 = new A();

            session.create(entity2);
            Assert.assertEquals(2, entity2.getId());
        }
    }

    @Test
    public void testQueryForAll() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            List<A> entities = new ArrayList<>();

            entities.add(new A());
            entities.add(new A());
            entities.add(new A());

            session.create(entities.toArray());
            Assert.assertEquals(session.queryForAll(A.class), entities);
        }
    }

    @Test
    public void testUpdate() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            A entity = new A();

            entity.setDesc("Test");
            session.create(entity);
            entity.setDesc("TestDesc");
            session.update(entity);

            A updatedEntity = session.queryForId(A.class, entity.getId());

            Assert.assertEquals(updatedEntity.getDesc(), "TestDesc");
        }
    }

    @Test
    public void testDelete() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            A entity = new A();

            Assert.assertEquals(1, session.create(entity));
            Assert.assertEquals(entity, session.queryForId(A.class, 1));

            Assert.assertEquals(1, session.delete(entity));
            Assert.assertNull(session.queryForId(A.class, 1));
        }
    }

    @Test
    public void testDeleteById() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            A entity = new A();

            Assert.assertEquals(1, session.create(entity));
            Assert.assertEquals(entity, session.queryForId(A.class, 1));

            Assert.assertEquals(1, session.deleteById(A.class, 1));
            Assert.assertNull(session.queryForId(A.class, 1));
        }
    }

    @Test
    public void testQueryForIdForeign() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            A entity = new A();

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
            A entity = new A();

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
            A entity = new A();

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
            A entity = new A();

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
            A a = new A();

            a.setDesc("Test");
            testForeignAutoCreateEntity.setA(a);
            session.create(testForeignAutoCreateEntity);
            ForeignAutoCreateForeignColumnTestEntity result = session.queryForId(
                    ForeignAutoCreateForeignColumnTestEntity.class,
                    1
            );

            Assert.assertEquals(result.getA(), a);
        }
    }

    @Test
    public void testForeignAutoCreateForForeignCollectionColumn() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            ForeignAutoCreateForeignCollectionColumnTestEntity testForeignAutoCreateEntity =
                    new ForeignAutoCreateForeignCollectionColumnTestEntity();
            A a = new A();

            a.setDesc("Test");
            testForeignAutoCreateEntity.getEntities().add(a);
            session.create(testForeignAutoCreateEntity);
            ForeignAutoCreateForeignCollectionColumnTestEntity result = session.queryForId(
                    ForeignAutoCreateForeignCollectionColumnTestEntity.class,
                    1
            );

            Assert.assertEquals(result.getEntities().get(0), a);
        }
    }

    @Test
    public void testRefreshForeign() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            ForeignTestEntity foreignTestEntity = new ForeignTestEntity();

            A a = new A();

            session.create(a);
            foreignTestEntity.setEntity(a);
            session.create(foreignTestEntity);
            ForeignTestEntity resultBefore = session.queryForId(ForeignTestEntity.class, 1);

            Assert.assertEquals(foreignTestEntity, resultBefore);

            a.setDesc("Test");
            session.update(a);
            session.refresh(resultBefore);
            Assert.assertEquals(resultBefore, foreignTestEntity);
        }
    }

    @Test
    public void testRefreshForeignCollection() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            ForeignCollectionTestEntity foreignTestEntity = new ForeignCollectionTestEntity();

            session.create(foreignTestEntity);
            A a = new A();

            a.setForeignCollectionTestEntity(foreignTestEntity);
            foreignTestEntity.getEntities().add(a);
            session.create(a);
            ForeignCollectionTestEntity resultBefore = session.queryForId(ForeignCollectionTestEntity.class, 1);

            Assert.assertEquals(foreignTestEntity.getEntities(), resultBefore.getEntities());

            a.setDesc("Test");
            session.update(a);
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

    @Test
    public void testExist() throws SQLException {
        try (Session session = createSessionAndCreateTables(WithDefaultTestEntity.class)) {
            WithDefaultTestEntity testEntity = new WithDefaultTestEntity();

            session.create(testEntity);
            Assert.assertTrue(session.exist(WithDefaultTestEntity.class, 1));
        }
    }

    @Test
    public void testCreateOrUpdateCreate() throws SQLException {
        try (Session session = createSessionAndCreateTables(WithDefaultTestEntity.class)) {
            WithDefaultTestEntity testEntity = new WithDefaultTestEntity();

            testEntity.setName("test");
            Dao.CreateOrUpdateStatus createOrUpdateStatus = session.createOrUpdate(testEntity);

            Assert.assertTrue(createOrUpdateStatus.isCreated());
            Assert.assertEquals(session.queryForId(WithDefaultTestEntity.class, 1), testEntity);
        }
    }

    @Test
    public void testCreateOrUpdateUpdate() throws SQLException {
        try (Session session = createSessionAndCreateTables(WithDefaultTestEntity.class)) {
            WithDefaultTestEntity testEntity = new WithDefaultTestEntity();

            session.create(testEntity);
            testEntity.setName("test");
            Dao.CreateOrUpdateStatus createOrUpdateStatus = session.createOrUpdate(testEntity);

            Assert.assertTrue(createOrUpdateStatus.isUpdated());
            Assert.assertEquals(session.queryForId(WithDefaultTestEntity.class, 1), testEntity);
        }
    }

    @Test
    public void testUniqueResult() throws SQLException {
        try (Session session = createSessionAndCreateTables(WithDefaultTestEntity.class)) {
            WithDefaultTestEntity testEntity = new WithDefaultTestEntity();

            testEntity.setName("Test");
            session.create(testEntity);
            WithDefaultTestEntity testEntity1 = new WithDefaultTestEntity();

            session.create(testEntity1);

            SelectStatement<WithDefaultTestEntity> selectStatement = new SelectStatement<>(WithDefaultTestEntity.class);

            selectStatement.where(new Criteria().add(Restrictions.eq("id", testEntity.getId())));
            Assert.assertEquals(session.uniqueResult(selectStatement), testEntity);
        }
    }

    @Test
    public void testList() throws SQLException {
        try (Session session = createSessionAndCreateTables(WithDefaultTestEntity.class)) {
            WithDefaultTestEntity testEntity = new WithDefaultTestEntity();

            testEntity.setName("Test");
            session.create(testEntity);
            WithDefaultTestEntity testEntity1 = new WithDefaultTestEntity();

            testEntity1.setName("Test1");
            session.create(testEntity1);

            SelectStatement<WithDefaultTestEntity> selectStatement = new SelectStatement<>(WithDefaultTestEntity.class);
            List<WithDefaultTestEntity> testEntities = session.list(selectStatement);

            Assert.assertEquals(2, testEntities.size());
            Assert.assertEquals(testEntity, testEntities.get(0));
            Assert.assertEquals(testEntity1, testEntities.get(1));
        }
    }

    @Test
    public void testQueryForLong() throws SQLException {
        try (Session session = createSessionAndCreateTables(WithDefaultTestEntity.class)) {
            WithDefaultTestEntity testEntity = new WithDefaultTestEntity();

            testEntity.setName("Test");
            session.create(testEntity);
            WithDefaultTestEntity testEntity1 = new WithDefaultTestEntity();

            testEntity1.setName("Test1");
            session.create(testEntity1);

            SelectStatement<WithDefaultTestEntity> selectStatement = new SelectStatement<>(WithDefaultTestEntity.class);

            selectStatement.countOff();
            long result = session.queryForLong(selectStatement);

            Assert.assertEquals(2, result);
        }
    }

    @Test
    public void testForeignReference() throws SQLException {
        try (Session session = createSessionAndCreateTables(UniqueFieldTestEntity.class, TableUniqueFieldTestEntity.class, ForeignFieldReferenceTestEntity.class)) {
            UniqueFieldTestEntity uniqueFieldTestEntity = new UniqueFieldTestEntity();

            uniqueFieldTestEntity.setName("Test");
            session.create(uniqueFieldTestEntity);
            ForeignFieldReferenceTestEntity referenceTestEntity = new ForeignFieldReferenceTestEntity();

            referenceTestEntity.setUniqueFieldTestEntity(uniqueFieldTestEntity);
            session.create(referenceTestEntity);
            Assert.assertEquals(referenceTestEntity, session.queryForId(ForeignFieldReferenceTestEntity.class, 1));
        }
    }

    @Test
    public void testDeleteStatement() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            session.create(new A());
            session.create(new A());
            session.create(new A());

            Assert.assertEquals(3, session.queryForAll(A.class).size());
            DeleteStatement deleteStatement = new DeleteStatement(A.class);

            deleteStatement.where(
                    new Criteria().add(Restrictions.eq("id", 2))
            );
            Assert.assertEquals(session.delete(deleteStatement), 1);
        }
    }

    @Test
    public void testUpdateStatement() throws SQLException {
        try (Session session = createSessionAndCreateTables()) {
            A a = new A();

            a.setDesc("Test");

            session.create(a);

            UpdateStatement updateStatement = new UpdateStatement(A.class);

            updateStatement
                    .set("desc", "Test1")
                    .where(new Criteria().add(Restrictions.eq("id", 1)));
            Assert.assertEquals(session.update(updateStatement), 1);
            Assert.assertEquals("Test1", session.queryForId(A.class, 1).getDesc());
        }
    }

    @Test
    public void testQuerySelectStatement() throws SQLException {
        try (Session session = createSessionAndCreateTables(WithDefaultTestEntity.class)) {
            WithDefaultTestEntity testEntity = new WithDefaultTestEntity();

            testEntity.setName("Test");
            session.create(testEntity);

            SelectStatement<WithDefaultTestEntity> selectStatement = new SelectStatement<>(WithDefaultTestEntity.class);

            selectStatement.select("id");
            selectStatement.withoutJoins(true);
            selectStatement.where(new Criteria()
                .add(Restrictions.eq("name", "Test"))
            );

            try (DatabaseResults databaseResults = session.query(selectStatement)) {
                while (databaseResults.next()) {
                    Assert.assertEquals(1, databaseResults.getInt("id"));
                }
            }
        }
    }

    @Test
    public void testListSelectStatement() throws SQLException {
        try (Session session = createSessionAndCreateTables(
                ForeignCollectionTestEntity.class,
                ForeignAutoCreateForeignCollectionColumnTestEntity.class,
                A.class)
        ) {
            ForeignCollectionTestEntity collectionTestEntity = new ForeignCollectionTestEntity();

            session.create(collectionTestEntity);
            A a = new A();

            a.setDesc("Test");
            a.setForeignCollectionTestEntity(collectionTestEntity);
            session.create(a);

            SelectStatement<A> selectStatement = new SelectStatement<>(A.class);

            selectStatement.select("id").select("foreignCollectionTestEntity");
            selectStatement.where(new Criteria()
                    .add(Restrictions.eq("desc", "Test"))
            );

            List<A> entities = session.list(selectStatement);

            Assert.assertEquals(1, entities.size());
            A result = entities.get(0);

            Assert.assertEquals(result.getId(), a.getId());
            Assert.assertNull(result.getDesc());
            Assert.assertNotNull(result.getForeignCollectionTestEntity());
        }
    }

    @Test
    public void testSelfJoin() throws SQLException {
        try (Session session = createSessionAndCreateTables(SelfJoinA.class)) {
            SelfJoinA a = new SelfJoinA();

            session.create(a);
            SelfJoinA a1 = new SelfJoinA();

            a1.setA(a);
            session.create(a1);
            SelfJoinA a2 = new SelfJoinA();

            a2.setA(a1);
            session.create(a2);
            SelfJoinA a3 = new SelfJoinA();

            a3.setA(a1);
            session.create(a3);

            SelfJoinA result = session.queryForId(SelfJoinA.class, 2);

            Assert.assertEquals(result.getId(), 2);
            Assert.assertEquals(result.getA(), a);
            Assert.assertEquals(result.getAs().size(), 2);
            Assert.assertEquals(result.getAs().get(0), a2);
            Assert.assertEquals(result.getAs().get(1), a3);
            Assert.assertEquals(result.getA().getAs().size(), 1);
            Assert.assertEquals(result.getA().getAs().get(0), a1);
        }
    }

    @Test
    public void testSelfJoinLazy() throws SQLException {
        try (Session session = createSessionAndCreateTables(SelfJoinLazyA.class)) {
            SelfJoinLazyA a = new SelfJoinLazyA();

            session.create(a);
            SelfJoinLazyA a1 = new SelfJoinLazyA();

            a1.setA(a);
            session.create(a1);
            SelfJoinLazyA a2 = new SelfJoinLazyA();

            a2.setA(a1);
            session.create(a2);
            SelfJoinLazyA a3 = new SelfJoinLazyA();

            a3.setA(a1);
            session.create(a3);

            SelfJoinLazyA result = session.queryForId(SelfJoinLazyA.class, 2);

            Assert.assertEquals(result.getId(), 2);
            Assert.assertEquals(result.getA(), a);
            Assert.assertEquals(result.getAs().size(), 2);
            Assert.assertEquals(result.getAs().get(0), a2);
            Assert.assertEquals(result.getAs().get(1), a3);
            Assert.assertEquals(result.getA().getAs().size(), 1);
            Assert.assertEquals(result.getA().getAs().get(0), a1);
        }
    }
}