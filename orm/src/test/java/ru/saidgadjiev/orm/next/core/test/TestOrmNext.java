package ru.saidgadjiev.orm.next.core.test;

import org.postgresql.ds.PGPoolingDataSource;
import ru.saidgadjiev.orm.next.core.dao.SessionManagerImpl;
import ru.saidgadjiev.orm.next.core.dao.SessionManager;
import ru.saidgadjiev.orm.next.core.db.PGDatabaseType;
import ru.saidgadjiev.orm.next.core.db.SerialTypeDataPersister;
import ru.saidgadjiev.orm.next.core.field.DataPersisterManager;
import ru.saidgadjiev.orm.next.core.logger.LoggerFactory;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.support.PolledConnectionSource;
import ru.saidgadjiev.orm.next.core.test.model.ormnext.A;
import ru.saidgadjiev.orm.next.core.test.model.ormnext.B;
import ru.saidgadjiev.orm.next.core.test.model.ormnext.C;
import ru.saidgadjiev.orm.next.core.utils.TableUtils;

import java.sql.SQLException;

public class TestOrmNext {

    public static void main(String[] args) throws SQLException {
        System.setProperty(LoggerFactory.LOG_ENABLED_PROPERTY, "true");
        SessionManager sessionManager = new SessionManagerImpl(postgreConnectionSource(), null);

        TableUtils.createTable(sessionManager.getDataSource(), C.class, true);
        TableUtils.createTable(sessionManager.getDataSource(), A.class, true);
        TableUtils.createTable(sessionManager.getDataSource(), B.class, true);
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
