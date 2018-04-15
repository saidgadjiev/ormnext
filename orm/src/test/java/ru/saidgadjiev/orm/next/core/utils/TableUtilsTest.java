package ru.saidgadjiev.orm.next.core.utils;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Assert;
import org.junit.Test;
import ru.saidgadjiev.orm.next.core.db.H2DatabaseType;
import ru.saidgadjiev.orm.next.core.field.DatabaseColumn;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.support.DataSourceConnectionSource;

/**
 * Created by said on 24.02.2018.
 */
public class TableUtilsTest {

    @Test
    public void createTableWithoutFields() throws Exception {
        Assert.assertTrue(TableUtils.createTable(getConnectionSource(), TestClazz.class, true));
    }

    @Test
    public void createTable() throws Exception {
    }

    @Test
    public void dropTable() throws Exception {
    }

    private ConnectionSource getConnectionSource() {
        JdbcDataSource dataSource = new JdbcDataSource();

        dataSource.setURL("jdbc:h2:mem:h2testdb");
        return new DataSourceConnectionSource(dataSource, new H2DatabaseType());
    }

    public static class TestClazz {
        @DatabaseColumn(id = true, generated = true)
        private int id;
    }
}