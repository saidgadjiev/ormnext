package ru.saidgadjiev.ormnext.core.dao;

import org.junit.Assert;
import org.junit.Test;
import ru.saidgadjiev.ormnext.core.BaseCoreTest;
import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;

import java.sql.SQLException;

/**
 * Created by said on 07.08.2018.
 */
public class TestForeignReference extends BaseCoreTest {

    @Test
    public void test() throws SQLException {
        try (Session session = createSessionAndCreateTables(AForeignCollection.class, A.class)) {
            AForeignCollection uniqueFieldTestEntity = new AForeignCollection();

            uniqueFieldTestEntity.setDesc("Test");
            session.create(uniqueFieldTestEntity);
            A referenceTestEntity = new A();

            referenceTestEntity.setaForeignCollection(uniqueFieldTestEntity);
            session.create(referenceTestEntity);
            Assert.assertEquals(referenceTestEntity, session.queryForId(A.class, 1));
        }
    }

    public static class A {

        @DatabaseColumn(id = true, generated = true)
        private int id;

        @DatabaseColumn
        private String desc;

        @ForeignColumn(foreignFieldName = "desc")
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

        @DatabaseColumn(unique = true)
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
}
