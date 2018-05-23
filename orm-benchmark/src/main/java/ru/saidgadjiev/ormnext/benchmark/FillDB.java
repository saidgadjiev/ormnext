package ru.saidgadjiev.ormnext.benchmark;

import org.apache.commons.lang.RandomStringUtils;
import org.postgresql.ds.PGPoolingDataSource;
import ru.saidgadjiev.ormnext.benchmark.domain.Order;
import ru.saidgadjiev.ormnext.benchmark.domain.UserProfile;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.dao.SessionManagerBuilder;
import ru.saidgadjiev.ormnext.core.field.DataPersisterManager;
import ru.saidgadjiev.ormnext.core.field.persister.SerialTypeDataPersister;
import ru.saidgadjiev.ormnext.core.support.ConnectionSource;

import java.sql.SQLException;

public class FillDB {

    public static void main(String[] args) throws SQLException {
        Session session = new SessionManagerBuilder()
                .entities(UserProfile.class, Order.class)
                .databaseType(new PGDatabaseType())
                .connectionSource(postgreConnectionSource())
                .build()
                .createSession();
        session.createTable(UserProfile.class, true);
        session.createTable(Order.class, true);

        for (int i = 0; i < 1000000; ++i) {
            UserProfile userProfile = new UserProfile();

            userProfile.setFirstName(RandomStringUtils.randomAlphabetic(10));
            userProfile.setLastName(RandomStringUtils.randomAlphabetic(10));
            userProfile.setMiddleName(RandomStringUtils.randomAlphabetic(10));
            session.create(userProfile);
            System.out.println(userProfile);
            for (int o = 0; o < 3; ++o) {
                Order order = new Order();

                order.setDescription(RandomStringUtils.randomAlphabetic(32));
                order.setUserProfile(userProfile);
                session.create(order);
                System.out.println(order);
            }
        }
    }

    private static ConnectionSource postgreConnectionSource() {
        DataPersisterManager.register(12, new SerialTypeDataPersister());
        PGPoolingDataSource dataSource = new PGPoolingDataSource();

        dataSource.setServerName("localhost");
        dataSource.setPortNumber(5432);
        dataSource.setUser("postgres");
        dataSource.setPassword("postgres");
        dataSource.setDatabaseName("benchmark");

        return new PolledConnectionSource(dataSource);
    }
}
