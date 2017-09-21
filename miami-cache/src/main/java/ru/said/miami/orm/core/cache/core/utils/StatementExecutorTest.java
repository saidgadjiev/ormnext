package ru.said.miami.orm.core.cache.core.utils;

import ru.said.miami.orm.core.cache.core.dao.StatementExecutor;
import ru.said.miami.cache.core.dialect.IDialect;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.said.miami.orm.core.cache.core.table.TableInfo;
import test_table.Foo;
import test_table.Foo1;
import test_table.Foo2;
import test_table.Foo3;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by said on 01.05.17.
 */
public class StatementExecutorTest {
    @Test
    public void execute() throws Exception {
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ConnectionSource connectionSource = createConnectionSource(connection, statement, mock(IDialect.class));
        String sql = "SELECT * FROM test";

        StatementExecutor.execute(connectionSource, sql);
        verify(connectionSource, times(1)).getConnection();
        verify(connectionSource, times(1)).releaseConnection(connection);
        verify(statement, times(1)).close();
        verify(statement, times(1)).execute(sql);
    }

    @Test
    public void queryForAll() throws Exception {
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ConnectionSource connectionSource = createConnectionSource(connection, statement, mock(IDialect.class));
        TableInfo<Foo> tableInfo = new TableInfo<>(Foo.class);

        ResultSet resultSet = mock(ResultSet.class);
        ResultSet resultSet1 = mock(ResultSet.class);
        ResultSet resultSet2 = mock(ResultSet.class);
        ResultSet resultSet3 = mock(ResultSet.class);
        ResultSet resultSet4 = mock(ResultSet.class);

        String sqlFoo = "SELECT * FROM foo WHERE id=1";
        String sqlFoo1 = "SELECT * FROM foo1 WHERE id=1";
        String sqlFoo2 = "SELECT * FROM foo2 WHERE id=1";
        String sqlForeignFoo = "SELECT id FROM foo2 WHERE foo_id=1";
        String sqlQueryForAll = "SELECT id FROM foo";

        when(resultSet.getObject("id")).thenReturn(1);
        when(resultSet.getObject("test_name")).thenReturn("test_foo");
        when(resultSet.getLong("foo1_id")).thenReturn(new Long(1));
        when(resultSet.next()).thenReturn(true).thenReturn(false);

        when(resultSet1.next()).thenReturn(true).thenReturn(false);
        when(resultSet1.getObject("id")).thenReturn(1);
        when(resultSet1.getObject("test_name")).thenReturn("test_foo1");
        when(statement.executeQuery(sqlFoo)).thenReturn(resultSet);
        when(statement.executeQuery(sqlFoo1)).thenReturn(resultSet1);

        when(resultSet2.next()).thenReturn(true).thenReturn(false);
        when(resultSet2.getObject("id")).thenReturn(1);
        when(resultSet2.getObject("test_name")).thenReturn("test_foo2");
        when(statement.executeQuery(sqlFoo2)).thenReturn(resultSet2);

        when(resultSet3.next()).thenReturn(true).thenReturn(false);
        when(resultSet3.getLong("id")).thenReturn(new Long(1));
        when(statement.executeQuery(sqlForeignFoo)).thenReturn(resultSet3);

        when(resultSet4.getLong("id")).thenReturn(new Long(1));
        when(resultSet4.next()).thenReturn(true).thenReturn(false);
        when(statement.executeQuery(sqlQueryForAll)).thenReturn(resultSet4);

        StatementExecutor statementExecutor = new StatementExecutor(connectionSource);
        List<Foo> testFoos = (List<Foo>) (List<?>) statementExecutor.queryForAll(tableInfo);

        Assert.assertEquals(testFoos.size(), 1);
        Assert.assertEquals(testFoos.get(0).getId(), 1);
        Assert.assertEquals(testFoos.get(0).getName(), "test_foo");
        List<Foo2> foo2List = testFoos.get(0).getFoo2List();

        Assert.assertEquals(foo2List.get(0).getId(), 1);
        Assert.assertEquals(foo2List.get(0).getName(), "test_foo2");
        Assert.assertEquals(foo2List.get(0).getFoo().getId(), testFoos.get(0).getId());
        Assert.assertEquals(foo2List.get(0).getFoo().getName(), testFoos.get(0).getName());
        verify(statement, times(1)).executeQuery(sqlFoo);
        verify(resultSet, times(1)).close();

        verify(statement, times(1)).executeQuery(sqlFoo1);
        verify(resultSet1, times(1)).close();

        verify(statement, times(1)).executeQuery(sqlFoo2);
        verify(resultSet2, times(1)).close();

        verify(statement, times(1)).executeQuery(sqlForeignFoo);
        verify(resultSet3, times(1)).close();

        verify(statement, times(1)).executeQuery(sqlQueryForAll);
        verify(resultSet4, times(1)).close();

        verify(connectionSource, times(4)).releaseConnection(connection);
    }

    @Test
    public void queryForAllWithSql() throws Exception {
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ConnectionSource connectionSource = createConnectionSource(connection, statement, mock(IDialect.class));
        TableInfo<Foo> tableInfo = new TableInfo<>(Foo.class);
        StatementExecutor statementExecutor = new StatementExecutor(connectionSource);
        String sql = "SELECT id FROM foo WHERE name='test_foo' ORDER BY ASC LIMIT 1";

        statementExecutor.queryForAll(tableInfo, sql);
        verify(statement, times(1)).executeQuery(sql);
    }

    @Test
    public void queryForWhere() throws Exception {
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ConnectionSource connectionSource = createConnectionSource(connection, statement, mock(IDialect.class));
        TableInfo<Foo> tableInfo = new TableInfo<>(Foo.class);
        StatementExecutor statementExecutor = new StatementExecutor(connectionSource);

        Where where = new Where();

        where.addEqClause("name", "test_foo");
        statementExecutor.queryForWhere(tableInfo, where);
        verify(statement, times(1)).executeQuery("SELECT id FROM foo WHERE name='test_foo'");
    }

    @Test
    public void queryForUpdate() throws Exception {
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ConnectionSource connectionSource = createConnectionSource(connection, statement, mock(IDialect.class));
        TableInfo<Foo> tableInfo = new TableInfo<>(Foo.class);
        StatementExecutor statementExecutor = new StatementExecutor(connectionSource);

        Update update = new Update();

        update.addUpdateColumn("name", "test_foo");
        Where where = new Where();

        where.addEqClause("id", 1);
        update.setWhere(where);
        statementExecutor.queryForUpdate(tableInfo, update);
        verify(statement, times(1)).executeUpdate("UPDATE foo SET name='test_foo' WHERE id='1'");
    }

    @Test
    public void queryForId() throws Exception {
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ConnectionSource connectionSource = createConnectionSource(connection, statement, mock(IDialect.class));
        TableInfo<Foo> tableInfo = new TableInfo<>(Foo.class);

        ResultSet resultSet = mock(ResultSet.class);
        ResultSet resultSet1 = mock(ResultSet.class);
        ResultSet resultSet2 = mock(ResultSet.class);
        ResultSet resultSet3 = mock(ResultSet.class);
        ResultSetMetaData resultSetMetaData = mock(ResultSetMetaData.class);
        ResultSetMetaData resultSetMetaData1 = mock(ResultSetMetaData.class);
        ResultSetMetaData resultSetMetaData2 = mock(ResultSetMetaData.class);
        ResultSetMetaData resultSetMetaData3 = mock(ResultSetMetaData.class);

        String sqlFoo = "SELECT * FROM foo WHERE id=1";
        String sqlFoo1 = "SELECT * FROM foo1 WHERE id=1";
        String sqlFoo2 = "SELECT * FROM foo2 WHERE id=1";
        String sqlForeignFoo = "SELECT id FROM foo2 WHERE foo_id=1";

        when(resultSet.getObject("id")).thenReturn(1);
        when(resultSet.getObject("test_name")).thenReturn("test_foo");
        when(resultSet.getObject("foo1_id")).thenReturn(1);

        when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
        when(resultSetMetaData.getColumnCount()).thenReturn(3);
        when(resultSetMetaData.getColumnName(0)).thenReturn("id");
        when(resultSetMetaData.getColumnName(1)).thenReturn("test_name");
        when(resultSetMetaData.getColumnName(2)).thenReturn("foo1_id");
        when(resultSet.next()).thenReturn(true).thenReturn(false);

        when(statement.executeQuery(sqlFoo)).thenReturn(resultSet);

        when(resultSet1.next()).thenReturn(true).thenReturn(false);
        when(resultSet1.getObject("id")).thenReturn(1);
        when(resultSet1.getObject("test_name")).thenReturn("test_foo1");

        when(resultSet1.getMetaData()).thenReturn(resultSetMetaData1);
        when(resultSetMetaData1.getColumnCount()).thenReturn(2);
        when(resultSetMetaData1.getColumnName(0)).thenReturn("id");
        when(resultSetMetaData1.getColumnName(1)).thenReturn("test_name");

        when(statement.executeQuery(sqlFoo1)).thenReturn(resultSet1);

        when(resultSet2.next()).thenReturn(true).thenReturn(false);
        when(resultSet2.getObject("id")).thenReturn(1);
        when(resultSet2.getObject("test_name")).thenReturn("test_foo2");

        when(statement.executeQuery(sqlFoo2)).thenReturn(resultSet2);

        when(resultSet2.getMetaData()).thenReturn(resultSetMetaData2);
        when(resultSetMetaData2.getColumnCount()).thenReturn(2);
        when(resultSetMetaData2.getColumnName(0)).thenReturn("id");
        when(resultSetMetaData2.getColumnName(1)).thenReturn("test_name");

        when(resultSet3.next()).thenReturn(true).thenReturn(false);
        when(resultSet3.getObject("id")).thenReturn(1);
        when(statement.executeQuery(sqlForeignFoo)).thenReturn(resultSet3);

        StatementExecutor statementExecutor = new StatementExecutor(connectionSource);
        Foo testFoo = (Foo) statementExecutor.queryForId(tableInfo, 1);

        verify(statement, times(1)).executeQuery(sqlFoo);
        verify(resultSet, times(1)).close();

        verify(statement, times(1)).executeQuery(sqlFoo1);
        verify(resultSet1, times(1)).close();

        verify(statement, times(1)).executeQuery(sqlFoo2);
        verify(resultSet2, times(1)).close();

        verify(statement, times(1)).executeQuery(sqlForeignFoo);
        verify(resultSet3, times(1)).close();

        verify(connectionSource, times(3)).releaseConnection(connection);

        //Without relations
        Assert.assertEquals("test_foo", testFoo.getName());
        Assert.assertEquals(1, testFoo.getId());
        //OneToOne
        Assert.assertEquals(testFoo.getFoo1().getName(), "test_foo1");
        Assert.assertEquals(1, testFoo.getFoo1().getId());
        List<Foo2> foo2List = new ArrayList<>();

        Foo2 foo2 = new Foo2();

        foo2.setId(1);
        foo2.setName("test_foo2");
        foo2List.add(foo2);
        //OneToMany
        Assert.assertEquals(testFoo.getFoo2List().size(), foo2List.size());
        Assert.assertArrayEquals(testFoo.getFoo2List().toArray(), foo2List.toArray());
    }

    @Test
    public void fillManyToMany() throws Exception {
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ConnectionSource connectionSource = createConnectionSource(connection, statement, mock(IDialect.class));
        TableInfo<Foo> tableInfo = new TableInfo<>(Foo.class);
        TableInfo<Foo3> tableInfo3 = new TableInfo<>(Foo3.class);

        ResultSet resultSet1 = mock(ResultSet.class);
        ResultSet resultSet2 = mock(ResultSet.class);
        ResultSet resultSet = mock(ResultSet.class);

        String sqlExist = "SELECT name FROM sqlite_master WHERE type='table' AND name='foo_foo3'";
        String sqlFoo3 = "SELECT * FROM foo3 WHERE id=1";
        String sql = "SELECT foo3_id FROM foo_foo3 WHERE foo_id=1";

        when(resultSet1.getString("name")).thenReturn("foo_foo3");
        when(resultSet1.next()).thenReturn(true).thenReturn(false);
        when(statement.executeQuery(sqlExist)).thenReturn(resultSet1);

        when(resultSet2.getLong("foo3_id")).thenReturn(new Long(1));
        when(resultSet2.next()).thenReturn(true).thenReturn(false);
        when(statement.executeQuery(sql)).thenReturn(resultSet2);

        when(statement.executeQuery(sqlFoo3)).thenReturn(resultSet);
        when(resultSet.getObject("id")).thenReturn(1);
        when(resultSet.getObject("test_name")).thenReturn("test_foo3");
        when(resultSet.next()).thenReturn(true).thenReturn(false);

        StatementExecutor statementExecutor = new StatementExecutor(connectionSource);
        Foo foo = new Foo();

        foo.setId(1);
        foo.setName("test_foo");

        for (Field field: tableInfo.getManyToManyRelations()){
            statementExecutor.fillManyToMany(tableInfo, tableInfo3, field, foo);
        }
        Assert.assertEquals(foo.getFoo3List().size(), 1);
        Assert.assertEquals(foo.getFoo3List().get(0).getId(), 1);
        Assert.assertEquals(foo.getFoo3List().get(0).getName(), "test_foo3");
        Assert.assertEquals(foo.getFoo3List().get(0).getFooList().get(0).getId(), 1);
        Assert.assertEquals(foo.getFoo3List().get(0).getFooList().get(0).getName(), "test_foo");

        verify(resultSet, times(1)).close();
        verify(resultSet1, times(1)).close();
        verify(resultSet2, times(1)).close();

        verify(statement, times(3)).close();
        verify(connectionSource, times(3)).releaseConnection(connection);
    }

    @Test
    public void create() throws Exception {
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        IDialect dialect = mock(IDialect.class);
        ConnectionSource connectionSource = createConnectionSource(connection, statement, dialect);
        StatementExecutor statementExecutor = new StatementExecutor(connectionSource);
        ResultSet resultSet = mock(ResultSet.class);
        ResultSet resultSet1 = mock(ResultSet.class);
        ResultSet resultSet2 = mock(ResultSet.class);
        ResultSet resultSet3 = mock(ResultSet.class);

        String lastInsertId = "SELECT " + connectionSource.getDialect().lastInsertId() + " AS last_id";
        String sqlExist2 = "SELECT name FROM sqlite_master WHERE type='table' AND name='foo3_foo'";
        String sqlExist1 = "SELECT name FROM sqlite_master WHERE type='table' AND name='foo_foo3'";
        String isExistRelationSql = "SELECT * from foo_foo3 WHERE foo_id=1 AND foo3_id=1";

        when(resultSet.getInt("last_id")).thenReturn(1);
        when(statement.executeQuery(lastInsertId)).thenReturn(resultSet);

        when(resultSet1.getString("name")).thenReturn("foo_foo3");
        when(resultSet1.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
        when(statement.executeQuery(sqlExist1)).thenReturn(resultSet1);

        when(resultSet2.getString("name")).thenReturn("");
        when(resultSet2.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
        when(statement.executeQuery(sqlExist2)).thenReturn(resultSet2);
        Foo3 foo3 = new Foo3();

        foo3.setName("test_foo3");
        statementExecutor.create(foo3);
        Assert.assertEquals(foo3.getId(), 1);
        verify(statement, times(1)).execute("INSERT INTO foo3(test_name) VALUES('test_foo3')");
        Foo1 foo1 = new Foo1();

        foo1.setName("test_foo1");
        statementExecutor.create(foo1);
        Assert.assertEquals(foo1.getId(), 1);
        verify(statement, times(1)).execute("INSERT INTO foo1(test_name) VALUES('test_foo1')");

        when(resultSet3.next()).thenReturn(false);
        when(statement.executeQuery(isExistRelationSql)).thenReturn(resultSet3);
        Foo foo = new Foo();

        foo.setName("test_foo");
        foo.getFoo3List().add(foo3);
        foo.setFoo1(foo1);

        statementExecutor.create(foo);
        Foo2 foo2 = new Foo2();

        foo2.setName("test_foo2");
        foo2.setFoo(foo);
        statementExecutor.create(foo2);
        Assert.assertEquals(foo2.getId(), 1);
        verify(statement, times(1)).execute("INSERT INTO foo2(test_name,foo_id) VALUES('test_foo2',1)");

        Assert.assertEquals(foo.getId(), 1);
        verify(resultSet, times(4)).close();
        verify(resultSet1, times(3)).close();
        verify(resultSet2, times(1)).close();
        verify(resultSet3, times(1)).close();
    }

    @Test
    public void getIdValue() throws Exception {

    }

    private ConnectionSource createConnectionSource(Connection connection, Statement statement, IDialect dialect) throws SQLException {
        ConnectionSource connectionSource = mock(ConnectionSource.class);

        when(statement.execute(anyString())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return null;
            }
        });
        when(connectionSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(connectionSource.getDialect()).thenReturn(dialect);
        when(dialect.lastInsertId()).thenReturn("last_insert_rowid()");

        return connectionSource;
    }

}