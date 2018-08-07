package ru.saidgadjiev.ormnext.core.dao;

import org.junit.Assert;
import org.junit.Test;
import ru.saidgadjiev.ormnext.core.BaseCoreTest;
import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.FetchType;
import ru.saidgadjiev.ormnext.core.field.ForeignCollectionField;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 06.08.2018.
 */
public class TestLazy extends BaseCoreTest {

    @Test
    public void test() throws SQLException {
        try (Session session = createSessionAndCreateTables(AForeignCollection.class, A.class)) {
            AForeignCollection testLazyCollection = new AForeignCollection();

            session.create(testLazyCollection);
            A testLazy = new A();

            testLazy.setDesc("TestLazy");
            testLazy.setaForeignCollection(testLazyCollection);

            session.create(testLazy);
            testLazyCollection.getAs().add(testLazy);
            AForeignCollection result = session.queryForId(AForeignCollection.class, 1);
            Assert.assertEquals(result.getAs(), testLazyCollection.getAs());

            A result1 = session.queryForId(A.class, 1);

            Assert.assertEquals(result1.getaForeignCollection(), testLazyCollection);
        }
    }

    public static class A {

        @DatabaseColumn(id = true, generated = true)
        private int id;

        @DatabaseColumn
        private String desc;

        @ForeignColumn(fetchType = FetchType.LAZY)
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

        @DatabaseColumn
        private String desc;

        @ForeignCollectionField(fetchType = FetchType.LAZY)
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

            return id == that.id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }
}
