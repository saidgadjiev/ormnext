package ru.saidgadjiev.ormnext.benchmark;

import org.apache.commons.lang.RandomStringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.postgresql.ds.PGPoolingDataSource;
import ru.saidgadjiev.ormnext.benchmark.domain.Order;
import ru.saidgadjiev.ormnext.benchmark.domain.UserProfile;
import ru.saidgadjiev.ormnext.core.connection_source.ConnectionSource;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.dao.SessionManager;
import ru.saidgadjiev.ormnext.core.dao.SessionManagerBuilder;
import ru.saidgadjiev.ormnext.core.field.DataPersisterManager;
import ru.saidgadjiev.ormnext.support.connection_source.PolledConnectionSource;
import ru.saidgadjiev.ormnext.support.data_persister.SerialTypeDataPersister;
import ru.saidgadjiev.ormnext.support.database_type.PGDatabaseType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Orm next benchmarks.
 *
 * @author said gadjiev
 */
@SuppressWarnings("magicnumber")
public class DaoBenchmark {

    /**
     * User profiles count.
     */
    private static final int BENCHMARK_USER_PROFILES_COUNT = 1000000;

    /**
     * Orders count for each user profile.
     */
    private static final int BENCHMARK_ORDERS_COUNT = 3;

    /**
     * Data source holds state.
     */
    @State(Scope.Benchmark)
    public static class DirectAccessState {

        /**
         * Data source.
         */
        private DataSource dataSource;

        /**
         * Create a new instance.
         */
        public DirectAccessState() {
            dataSource = dataSource();
        }
    }

    /**
     * Session manager holds state.
     */
    @State(Scope.Benchmark)
    public static class DaoState {

        /**
         * Session manager.
         */
        private SessionManager sessionManager;

        /**
         * Set up.
         */
        @Setup(Level.Trial)
        public void doSetUp() {
            sessionManager = new SessionManagerBuilder()
                    .entities(UserProfile.class, Order.class)
                    .databaseType(new PGDatabaseType())
                    .connectionSource(new PolledConnectionSource(dataSource()))
                    .build();
        }

        /**
         * Tear down.
         * @throws SQLException when close connection source {@link ConnectionSource#close()}
         */
        @TearDown
        public void doTearDown() throws SQLException {
            sessionManager.close();
        }
    }

    /**
     * Dao query for all benchmark {@link ru.saidgadjiev.ormnext.core.dao.BaseDao#queryForAll(Class)}.
     * @param daoState dao state {@link DaoState}
     * @return return result
     * @throws SQLException any SQL exceptions
     */
    @Benchmark
    @Warmup(iterations = 0)
    @Measurement(iterations = 5)
    @BenchmarkMode(value = Mode.AverageTime)
    @OutputTimeUnit(value = TimeUnit.MILLISECONDS)
    @Fork(0)
    public Object testQueryForAll(DaoState daoState) throws SQLException {
        try (Session session = daoState.sessionManager.createSession()) {
            return session.queryForAll(UserProfile.class);
        }
    }

    /**
     * Direct access query for all benchmark.
     * @param directAccessState state {@link DirectAccessState}
     * @return result
     * @throws Exception any exceptions
     */
    @Benchmark
    @Warmup(iterations = 0)
    @Measurement(iterations = 5)
    @BenchmarkMode(value = Mode.AverageTime)
    @OutputTimeUnit(value = TimeUnit.MILLISECONDS)
    @Fork(0)
    public Object testQueryForAllDirectAccess(DirectAccessState directAccessState) throws Exception {
        List<UserProfile> userProfiles = new ArrayList<>();
        String sql = "select u.id as uid, u.firstname as first, u.lastname as last, u.middlename as mid, "
                + "o.id as oid, o.description as odes, o.date as odate, o.userprofile_id as ous from userprofile u "
                + "LEFT JOIN \"order\" o ON u.id = o.userprofile_id";

        try (Connection connection = directAccessState.dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        UserProfile userProfile = new UserProfile();

                        userProfile.setId(resultSet.getInt("uid"));
                        userProfile.setFirstName(resultSet.getString("first"));
                        userProfile.setLastName(resultSet.getString("last"));
                        userProfile.setMiddleName(resultSet.getString("mid"));

                        Order order = new Order();

                        order.setId(resultSet.getInt("oid"));
                        order.setDescription(resultSet.getString("odes"));
                        order.setDate(new Date(resultSet.getTimestamp("odate").getTime()));
                        order.setUserProfile(userProfile);
                        userProfile.getOrders().add(order);
                        userProfiles.add(userProfile);
                    }
                }
            }
        }

        System.out.println(userProfiles.size());

        return userProfiles;
    }

    /**
     * Fill db.
     * @throws SQLException any SQL exceptions
     */
    private static void fillDb() throws SQLException {
        SessionManager sessionManager = sessionManager();

        try (Session session = sessionManager.createSession()) {
            session.createTable(UserProfile.class, true);
            session.createTable(Order.class, true);

            if (session.countOff(UserProfile.class) == BENCHMARK_USER_PROFILES_COUNT) {
                return;
            }
            for (int i = 0; i < BENCHMARK_USER_PROFILES_COUNT; ++i) {
                UserProfile userProfile = new UserProfile();

                userProfile.setFirstName(RandomStringUtils.randomAlphabetic(10));
                userProfile.setLastName(RandomStringUtils.randomAlphabetic(10));
                userProfile.setMiddleName(RandomStringUtils.randomAlphabetic(10));
                session.create(userProfile);
                System.out.println(userProfile);
                for (int o = 0; o < BENCHMARK_ORDERS_COUNT; ++o) {
                    Order order = new Order();

                    order.setDescription(RandomStringUtils.randomAlphabetic(32));
                    order.setUserProfile(userProfile);
                    session.create(order);
                    System.out.println(order);
                }
            }
        }
    }

    /**
     * Create data source.
     * @return pg data source
     */
    private static DataSource dataSource() {
        DataPersisterManager.register(SerialTypeDataPersister.SERIAL, new SerialTypeDataPersister());
        PGPoolingDataSource dataSource = new PGPoolingDataSource();

        dataSource.setServerName("localhost");
        dataSource.setPortNumber(5432);
        dataSource.setUser("postgres");
        dataSource.setPassword("postgres");
        dataSource.setDatabaseName("benchmark");

        return dataSource;
    }

    /**
     * Create session manager.
     * @return session manager
     */
    private static SessionManager sessionManager() {
        return new SessionManagerBuilder()
                .entities(UserProfile.class, Order.class)
                .databaseType(new PGDatabaseType())
                .connectionSource(new PolledConnectionSource(dataSource()))
                .build();
    }

    /**
     * Start benchmark.
     * @param args args
     * @throws Exception any exceptions
     */
    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(DaoBenchmark.class.getSimpleName())
                .shouldFailOnError(true)
                .build();

        fillDb();
        new Runner(opt).run();
    }

}
