package ru.saidgadjiev.ormnext.core.dao;

import org.junit.Assert;
import org.junit.Test;
import ru.saidgadjiev.ormnext.core.BaseCoreTest;
import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.ForeignCollectionField;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.Criteria;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.Restrictions;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 06.08.2018.
 */
public class TestListWithSelectColumns extends BaseCoreTest {

    @Test
    public void test() throws SQLException {
        try (Session session = createSessionAndCreateTables(AForeignCollection.class, A.class)) {
            AForeignCollection collectionTestEntity = new AForeignCollection();

            session.create(collectionTestEntity);
            A a = new A();

            a.setDesc("Test");
            a.setaForeignCollection(collectionTestEntity);
            session.create(a);

            SelectStatement<A> selectStatement = session.statementBuilder().createSelectStatement(A.class);

            selectStatement.select("id").select("aForeignCollection");
            selectStatement.where(new Criteria()
                    .add(Restrictions.eq("desc", "Test"))
            );

            List<A> entities = selectStatement.list();

            Assert.assertEquals(1, entities.size());
            A result = entities.get(0);

            Assert.assertEquals(result.getId(), a.getId());
            Assert.assertNull(result.getDesc());
            Assert.assertNotNull(result.getaForeignCollection());
        }
    }

    public static class A {

        @DatabaseColumn(id = true, generated = true)
        private int id;

        @DatabaseColumn
        private String desc;

        @ForeignColumn
        private AForeignCollection aForeignCollection;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public AForeignCollection getaForeignCollection() {
            return aForeignCollection;
        }

        public void setaForeignCollection(AForeignCollection aForeignCollection) {
            this.aForeignCollection = aForeignCollection;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            A a = (A) o;

            if (id != a.id) return false;
            if (desc != null ? !desc.equals(a.desc) : a.desc != null) return false;
            return aForeignCollection != null ? aForeignCollection.equals(a.aForeignCollection) : a.aForeignCollection == null;
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (desc != null ? desc.hashCode() : 0);
            result = 31 * result + (aForeignCollection != null ? aForeignCollection.hashCode() : 0);
            return result;
        }
    }

    public static class AForeignCollection {

        @DatabaseColumn(id = true, generated = true)
        private int id;

        @ForeignCollectionField
        private List<A> as = new ArrayList<>();

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setAs(List<A> as) {
            this.as = as;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AForeignCollection that = (AForeignCollection) o;

            return id == that.id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }
}
