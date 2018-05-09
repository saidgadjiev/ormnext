package ru.saidgadjiev.ormnext.core.test;

import org.postgresql.ds.PGPoolingDataSource;
import ru.saidgadjiev.ormnext.core.criteria.impl.CriteriaQuery;
import ru.saidgadjiev.ormnext.core.criteria.impl.OrderByCriteria;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.dao.SessionManager;
import ru.saidgadjiev.ormnext.core.dao.SessionManagerBuilder;
import ru.saidgadjiev.ormnext.core.db.PGDatabaseType;
import ru.saidgadjiev.ormnext.core.field.DataPersisterManager;
import ru.saidgadjiev.ormnext.core.field.persister.SerialTypeDataPersister;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.ormnext.core.support.ConnectionSource;
import ru.saidgadjiev.ormnext.core.support.PolledConnectionSource;
import ru.saidgadjiev.ormnext.core.test.model.ormnext.A;
import ru.saidgadjiev.ormnext.core.test.model.ormnext.B;
import ru.saidgadjiev.ormnext.core.test.model.ormnext.C;
import ru.saidgadjiev.ormnext.core.test.model.ormnext.D;

import java.sql.SQLException;
import java.util.List;

public class TestOrmNext {

    public static void main(String[] args) throws SQLException {
        System.setProperty(LoggerFactory.LOG_ENABLED_PROPERTY, "true");
        SessionManager sessionManager = new SessionManagerBuilder()
                .addEntityClasses(B.class, A.class, C.class, D.class)
                .databaseType(new PGDatabaseType())
                .connectionSource(postgreConnectionSource())
                .build();
        Session session = sessionManager.createSession();

        session.createTable(C.class, true);
        session.createTable(A.class, true);
        session.createTable(B.class, true);
        session.createTable(D.class, true);

        try {
            List<B> b = session.queryForAll(B.class);

            System.out.println(b);
        } catch (SQLException ex) {
            session.rollback();

            throw ex;
        }
        session.close();
        sessionManager.close();
    }

    private static void testCriteriaQuery(Session session) throws SQLException {
        List<B> objects = session.list(new CriteriaQuery<>(B.class)
                .orderBy(new OrderByCriteria()
                    .add(false, "name"))
                .limit(10)
                .offset(0));

        objects.forEach(object -> System.out.println(object.getdSet().size()));
    }

    private static ConnectionSource postgreConnectionSource() {
        DataPersisterManager.register(12, new SerialTypeDataPersister());
        PGPoolingDataSource dataSource = new PGPoolingDataSource();

        dataSource.setServerName("localhost");
        dataSource.setPortNumber(5432);
        dataSource.setUser("postgres");
        dataSource.setPassword("postgres");
        dataSource.setDatabaseName("ormtest");

        return new PolledConnectionSource(dataSource);
    }
}
