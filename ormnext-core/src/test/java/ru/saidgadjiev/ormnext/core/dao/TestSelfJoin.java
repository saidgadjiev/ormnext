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
 * Created by said on 06.08.2018.
 */
public class TestSelfJoin extends BaseCoreTest {

    @Test
    public void test() throws SQLException {
        try (Session session = createSessionAndCreateTables(A.class)) {
            A a = new A();

            session.create(a);
            A a1 = new A();

            a1.setA(a);
            session.create(a1);
            A a2 = new A();

            a2.setA(a1);
            session.create(a2);
            A a3 = new A();

            a3.setA(a1);
            session.create(a3);

            A result = session.queryForId(A.class, 2);

            Assert.assertEquals(result.getId(), 2);
            Assert.assertEquals(result.getA(), a);
            Assert.assertEquals(result.getAs().size(), 2);
            Assert.assertEquals(result.getAs().get(0), a2);
            Assert.assertEquals(result.getAs().get(1), a3);
            Assert.assertEquals(result.getA().getAs().size(), 1);
            Assert.assertEquals(result.getA().getAs().get(0), a1);
        }
    }

    public static class A {

        @DatabaseColumn(id = true, generated = true)
        private int id;

        @ForeignColumn
        private A a;

        @ForeignCollectionField
        private List<A> as = new ArrayList<>();

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public A getA() {
            return a;
        }

        public void setA(A a) {
            this.a = a;
        }

        public List<A> getAs() {
            return as;
        }

        public void setAs(List<A> as) {
            this.as = as;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            A a = (A) o;

            return id == a.id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }
}
