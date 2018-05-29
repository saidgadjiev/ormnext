package ru.saidgadjiev.ormnext.core;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import ru.saidgadjiev.ormnext.core.connection_source.DataSourceConnectionSource;
import ru.saidgadjiev.ormnext.core.dao.SessionManager;
import ru.saidgadjiev.ormnext.core.dao.SessionManagerBuilder;
import ru.saidgadjiev.ormnext.core.database_type.H2DatabaseType;
import ru.saidgadjiev.ormnext.core.model.ForeignSimpleEntity;
import ru.saidgadjiev.ormnext.core.model.SimpleEntity;

import java.sql.SQLException;

public class BaseCoreTest {

    protected static SessionManager sessionManager;

    @BeforeClass
    public static void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();

        dataSource.setURL("jdbc:h2:mem:h2testdatabase");
        sessionManager = new SessionManagerBuilder()
                .entities(SimpleEntity.class, ForeignSimpleEntity.class)
                .databaseType(new H2DatabaseType())
                .connectionSource(new DataSourceConnectionSource(dataSource))
                .build();
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        sessionManager.close();
    }
}
