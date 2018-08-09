package ru.saidgadjiev.ormnext.core.cache;

import ru.saidgadjiev.ormnext.core.BaseCoreTest;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.dao.SessionManager;
import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.ForeignCollectionField;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;
import ru.saidgadjiev.ormnext.core.util.TestUtils;
import ru.saidgajiev.ormnext.cache.CacheImpl;
import ru.saidgajiev.ormnext.cache.Cacheable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 09.08.2018.
 */
public class Test {

    @org.junit.Test
    public void test() throws SQLException {
        try (SessionManager sessionManager = TestUtils.h2SessionManager(A.class, C.class, B.class)) {
            sessionManager.upgrade(new CacheImpl());

            sessionManager.enableDefaultCache();
            try (Session session = sessionManager.createSession()) {
                session.createTables(new Class[] {C.class, B.class, A.class}, true);

                C c = new C();

                session.create(c);
                B b = new B();

                b.setC(c);
                session.create(b);
                A a = new A();

                a.setB(b);

                session.create(a);

                session.queryForId(A.class, 1);

                session.queryForId(A.class, 1);
            }
        }
    }

    @Cacheable
    public static class A {

        @DatabaseColumn(id = true, generated = true)
        private int id;

        @ForeignColumn
        private B b;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public B getB() {
            return b;
        }

        public void setB(B b) {
            this.b = b;
        }
    }

    @Cacheable
    public static class B {

        @DatabaseColumn(id = true, generated = true)
        private int id;

        @ForeignColumn
        private C c;

        @ForeignCollectionField
        private List<A> as = new ArrayList<>();

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public C getC() {
            return c;
        }

        public void setC(C c) {
            this.c = c;
        }
    }

    @Cacheable
    public static class C {

        @DatabaseColumn(id = true, generated = true)
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
