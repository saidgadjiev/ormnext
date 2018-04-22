package ru.saidgadjiev.orm.next.core.test;

import org.postgresql.ds.PGPoolingDataSource;
import ru.saidgadjiev.orm.next.core.criteria.impl.Criteria;
import ru.saidgadjiev.orm.next.core.criteria.impl.OrderByCriteria;
import ru.saidgadjiev.orm.next.core.criteria.impl.Restrictions;
import ru.saidgadjiev.orm.next.core.dao.Session;
import ru.saidgadjiev.orm.next.core.dao.SessionManager;
import ru.saidgadjiev.orm.next.core.dao.SessionMangerBuilder;
import ru.saidgadjiev.orm.next.core.db.PGDatabaseType;
import ru.saidgadjiev.orm.next.core.db.SerialTypeDataPersister;
import ru.saidgadjiev.orm.next.core.field.DataPersisterManager;
import ru.saidgadjiev.orm.next.core.logger.LoggerFactory;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.support.PolledConnectionSource;
import ru.saidgadjiev.orm.next.core.test.model.ormnext.A;
import ru.saidgadjiev.orm.next.core.test.model.ormnext.B;
import ru.saidgadjiev.orm.next.core.test.model.ormnext.C;
import ru.saidgadjiev.orm.next.core.test.model.ormnext.D;

import java.sql.SQLException;
import java.util.List;

import static ru.saidgadjiev.orm.next.core.criteria.impl.Restrictions.*;

public class TestOrmNext {

    public static void main(String[] args) throws SQLException {
        System.setProperty(LoggerFactory.LOG_ENABLED_PROPERTY, "true");
        SessionManager sessionManager = new SessionMangerBuilder()
                .addEntityClasses(B.class, A.class, C.class, D.class)
                .connectionSource(postgreConnectionSource())
                .build();

        Session session = sessionManager.getCurrentSession();
        testCriteriaQuery(session);

        session.close();
    }

    private static void testCriteriaQuery(Session session) throws SQLException {
        List<B> objects = session
                .criteriaQuery(B.class)
                .orderBy(new OrderByCriteria()
                    .add(false, "name"))
                .limit(10)
                .offset(0)
                .list();

        objects.forEach(System.out::println);
    }

    private static ConnectionSource postgreConnectionSource() {
        DataPersisterManager.register(8, new SerialTypeDataPersister());
        PGPoolingDataSource dataSource = new PGPoolingDataSource();

        dataSource.setServerName("localhost");
        dataSource.setPortNumber(5432);
        dataSource.setUser("postgres");
        dataSource.setPassword("postgres");
        dataSource.setDatabaseName("ormtest");

        return new PolledConnectionSource(dataSource, new PGDatabaseType());
    }
}
