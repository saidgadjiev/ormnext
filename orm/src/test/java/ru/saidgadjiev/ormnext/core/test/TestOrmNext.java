package ru.saidgadjiev.ormnext.core.test;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import org.postgresql.ds.PGPoolingDataSource;
import ru.saidgadjiev.ormnext.core.criteria.impl.CriteriaQuery;
import ru.saidgadjiev.ormnext.core.criteria.impl.OrderByCriteria;
import ru.saidgadjiev.ormnext.core.dao.Dao;
import ru.saidgadjiev.ormnext.core.dao.DaoBuilder;
import ru.saidgadjiev.ormnext.core.db.PGDatabaseType;
import ru.saidgadjiev.ormnext.core.db.SerialTypeDataPersister;
import ru.saidgadjiev.ormnext.core.field.DataPersisterManager;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.ormnext.core.support.ConnectionSource;
import ru.saidgadjiev.ormnext.core.support.PolledConnectionSource;
import ru.saidgadjiev.ormnext.core.test.model.ormnext.A;
import ru.saidgadjiev.ormnext.core.test.model.ormnext.B;
import ru.saidgadjiev.ormnext.core.test.model.ormnext.C;
import ru.saidgadjiev.ormnext.core.test.model.ormnext.D;
import ru.saidgadjiev.ormnext.core.dao.Dao;
import ru.saidgadjiev.ormnext.core.dao.DaoBuilder;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.ormnext.core.support.ConnectionSource;
import ru.saidgadjiev.ormnext.core.test.model.ormnext.A;
import ru.saidgadjiev.ormnext.core.test.model.ormnext.B;
import ru.saidgadjiev.ormnext.core.test.model.ormnext.C;
import ru.saidgadjiev.ormnext.core.test.model.ormnext.D;

import java.sql.SQLException;
import java.util.List;

public class TestOrmNext {

    public static void main(String[] args) throws SQLException {
        System.setProperty(LoggerFactory.LOG_ENABLED_PROPERTY, "true");
        Dao dao = new DaoBuilder()
                .addEntityClasses(B.class, A.class, C.class, D.class)
                .databaseType(new PGDatabaseType())
                .connectionSource(postgreConnectionSource())
                .build();

        B b = dao.queryForId(B.class, 0);

        System.out.println(b.getdSet().size());
        dao.close();
    }

    private static void testCriteriaQuery(Dao dao) throws SQLException {
        List<B> objects = dao.list(new CriteriaQuery<>(B.class)
                .orderBy(new OrderByCriteria()
                    .add(false, "name"))
                .limit(10)
                .offset(0));

        objects.forEach(object -> System.out.println(object.getdSet().size()));
    }

    private static ConnectionSource postgreConnectionSource() {
        DataPersisterManager.register(8, new SerialTypeDataPersister());
        PGPoolingDataSource dataSource = new PGPoolingDataSource();

        dataSource.setServerName("localhost");
        dataSource.setPortNumber(5432);
        dataSource.setUser("postgres");
        dataSource.setPassword("postgres");
        dataSource.setDatabaseName("ormtest");

        return new PolledConnectionSource(dataSource);
    }
}
