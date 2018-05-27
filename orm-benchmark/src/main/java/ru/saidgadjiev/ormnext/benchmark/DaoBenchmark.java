/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package ru.saidgadjiev.ormnext.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.postgresql.ds.PGPoolingDataSource;
import ru.saidgadjiev.ormnext.benchmark.domain.Order;
import ru.saidgadjiev.ormnext.benchmark.domain.UserProfile;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.dao.SessionManagerBuilder;
import ru.saidgadjiev.ormnext.core.field.DataPersisterManager;
import ru.saidgadjiev.ormnext.core.connection_source.ConnectionSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DaoBenchmark {

    @State(Scope.Benchmark)
    public static class DirectAccessState {

        private PGPoolingDataSource dataSource;

        public DirectAccessState() {
            dataSource = new PGPoolingDataSource();

            dataSource.setServerName("localhost");
            dataSource.setPortNumber(5432);
            dataSource.setUser("postgres");
            dataSource.setPassword("postgres");
            dataSource.setDatabaseName("benchmark");
        }
    }

    @State(Scope.Benchmark)
    public static class DaoState {

        private Session session;

        @Setup(Level.Trial)
        public void doSetUp() throws SQLException {
            session = new SessionManagerBuilder()
                    .entities(UserProfile.class, Order.class)
                    .databaseType(new PGDatabaseType())
                    .connectionSource(postgreConnectionSource())
                    .build()
                    .createSession();
        }

        @TearDown
        public void doTearDown() throws SQLException {
            session.close();
        }

        public Session getSession() {
            return session;
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

    @Benchmark
    @Warmup(iterations = 0)
    @Measurement(iterations = 5)
    @BenchmarkMode(value = Mode.AverageTime)
    @OutputTimeUnit(value = TimeUnit.MILLISECONDS)
    @Fork(0)
    public Object testQueryForAll(DaoState daoState) throws SQLException {
        return daoState.getSession().queryForAll(UserProfile.class);
    }

    /*@Benchmark
    @Warmup(iterations = 0)
    @Measurement(iterations = 5)
    @BenchmarkMode(value = Mode.AverageTime)
    @OutputTimeUnit(value = TimeUnit.MILLISECONDS)
    @Fork(0)*/
    public Object testQueryForAllDirectAccess(DirectAccessState directAccessState) throws Exception {
        List<UserProfile> userProfiles = new ArrayList<>();
        String sql = "select u.id as uid, u.firstname as first, u.lastname as last, u.middlename as mid, o.id as oid, o.description as odes, o.date as odate, o.userprofile_id as ous from userprofile u LEFT JOIN \"order\" o ON u.id = o.userprofile_id";

        try (Connection connection = directAccessState.dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while(resultSet.next()) {
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

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(DaoBenchmark.class.getSimpleName())
                .shouldFailOnError(true)
                .build();

        new Runner(opt).run();
    }

}
