package clause;

import dao.Dao;
import dao.DaoImpl;
import db.SQLiteConnectionSource;
import db.dialect.IDialect;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import support.JDBCConnectionSource;
import test_table.Foo;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by said on 07.05.17.
 */
public class QueryBuilderTest {

    @Test
    public void limit() throws Exception {
        QueryBuilder<Foo> queryBuilder = new QueryBuilder<>(Foo.class, mock(Dao.class));

        queryBuilder.limit(1);
        Assert.assertEquals("SELECT id FROM foo LIMIT 1", queryBuilder.getStringQuery());
    }

    @Test
    public void orderBy() throws Exception {
        QueryBuilder<Foo> queryBuilder = new QueryBuilder<>(Foo.class, mock(Dao.class));

        queryBuilder.orderBy(QueryBuilder.ORDER_BY.ASC);
        Assert.assertEquals("SELECT id FROM foo ORDER BY ASC", queryBuilder.getStringQuery());
    }

    @Test
    public void where() throws Exception {
        QueryBuilder<Foo> queryBuilder = new QueryBuilder<>(Foo.class, mock(Dao.class));

        Where where = new Where();

        where.addEqClause("name", "test_foo");
        queryBuilder.where(where);
        Assert.assertEquals("SELECT id FROM foo WHERE name='test_foo'", queryBuilder.getStringQuery());
    }

    @Test
    public void executeQuery() throws Exception {

    }

    @Test
    public void getStringQuery() throws Exception {
        QueryBuilder<Foo> queryBuilder = new QueryBuilder<>(Foo.class, mock(Dao.class));
        Where where = new Where();

        where.addEqClause("name", "test_foo");
        queryBuilder.where(where);
        queryBuilder.orderBy(QueryBuilder.ORDER_BY.ASC);
        queryBuilder.limit(1);

        Assert.assertEquals("SELECT id FROM foo WHERE name='test_foo' ORDER BY ASC LIMIT 1", queryBuilder.getStringQuery());

    }

    private Dao<Foo> createDao(JDBCConnectionSource connectionSource) {
        return new DaoImpl<>(connectionSource, Foo.class);
    }

}