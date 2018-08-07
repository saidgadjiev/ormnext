package ru.saidgadjiev.ormnext.core.dao;

import org.junit.Assert;
import org.junit.Test;
import ru.saidgadjiev.ormnext.core.BaseCoreTest;
import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;

import java.sql.SQLException;

/**
 * Created by said on 06.08.2018.
 */
public class TestUpdate extends BaseCoreTest {

    @Test
    public void test() throws SQLException {
        try (Session session = createSessionAndCreateTables(A.class)) {
            A entity = new A();

            entity.setDesc("Test");
            session.create(entity);
            entity.setDesc("TestDesc");
            session.update(entity);

            A updatedEntity = session.queryForId(A.class, entity.getId());

            Assert.assertEquals(updatedEntity.getDesc(), "TestDesc");
        }
    }

    public static class A {

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
}
