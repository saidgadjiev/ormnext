package ru.saidgadjiev.ormnext.core.dao;

import org.junit.Assert;
import org.junit.Test;
import ru.saidgadjiev.ormnext.core.BaseCoreTest;
import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.ForeignCollectionField;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 07.08.2018.
 */
public class TestForeignAutoCreate extends BaseCoreTest {

    @Test
    public void testForeignAutoCreateCollection() throws SQLException {
        try (Session session = createSessionAndCreateTables(AForeignCollection.class, AForeign.class, A.class)) {
            AForeignCollection testForeignAutoCreateEntity =
                    new AForeignCollection();
            A a = new A();

            a.setDesc("Test");
            testForeignAutoCreateEntity.getAs().add(a);
            session.create(testForeignAutoCreateEntity);
            AForeignCollection result = session.queryForId(
                    AForeignCollection.class,
                    1
            );

            Assert.assertEquals(result.getAs().get(0), a);
        }
    }

    @Test
    public void testForeignAutoCreate() throws SQLException {
        try (Session session = createSessionAndCreateTables(AForeignCollection.class, AForeign.class, A.class)) {
            A testForeignAutoCreateEntity =
                    new A();
            AForeign a = new AForeign();

            a.setDesc("Test");
            testForeignAutoCreateEntity.setaForeign(a);
            session.create(testForeignAutoCreateEntity);
            A result = session.queryForId(
                    A.class,
                    1
            );

            Assert.assertEquals(result.getaForeign(), a);
        }
    }

    public static class A {

        @DatabaseColumn(id = true, generated = true)
        private int id;

        @DatabaseColumn
        private String desc;

        @ForeignColumn
        private AForeignCollection aForeignCollection;

        @ForeignColumn(foreignAutoCreate = true)
        private AForeign aForeign;

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

        public AForeign getaForeign() {
            return aForeign;
        }

        public void setaForeign(AForeign aForeign) {
            this.aForeign = aForeign;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            A a = (A) o;

            if (id != a.id) return false;
            return desc != null ? desc.equals(a.desc) : a.desc == null;
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (desc != null ? desc.hashCode() : 0);
            return result;
        }
    }

    public static class AForeignCollection {

        @DatabaseColumn(id = true, generated = true)
        private int id;

        @DatabaseColumn
        private String desc;

        @ForeignCollectionField(foreignAutoCreate = true)
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

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public List<A> getAs() {
            return as;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AForeignCollection that = (AForeignCollection) o;

            if (id != that.id) return false;
            return desc != null ? desc.equals(that.desc) : that.desc == null;
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (desc != null ? desc.hashCode() : 0);
            return result;
        }
    }

    public static class AForeign {

        @DatabaseColumn(id = true, generated = true)
        private int id;

        @DatabaseColumn
        private String desc;

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AForeign that = (AForeign) o;

            if (id != that.id) return false;
            return desc != null ? desc.equals(that.desc) : that.desc == null;
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (desc != null ? desc.hashCode() : 0);
            return result;
        }
    }
}
