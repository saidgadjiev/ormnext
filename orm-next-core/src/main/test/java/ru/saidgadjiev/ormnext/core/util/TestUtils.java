package ru.saidgadjiev.ormnext.core.util;

import org.h2.jdbcx.JdbcDataSource;
import ru.saidgadjiev.ormnext.core.connection.source.DataSourceConnectionSource;
import ru.saidgadjiev.ormnext.core.dao.SessionManager;
import ru.saidgadjiev.ormnext.core.dao.SessionManagerBuilder;
import ru.saidgadjiev.ormnext.core.dialect.H2Dialect;

import java.sql.SQLException;

public final class TestUtils {

    private TestUtils() {
    }

    public static SessionManager h2SessionManager(Class<?>... entityClasses) throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();

        dataSource.setURL("jdbc:h2:mem:h2testdatabase");
        return new SessionManagerBuilder()
                .entities(entityClasses).databaseType(new H2Dialect())
                .connectionSource(new DataSourceConnectionSource(dataSource))
                .build();
    }
}
