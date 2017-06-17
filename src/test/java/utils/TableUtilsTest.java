package utils;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import table.TableInfo;
import test_table.Foo;
import test_table.Foo3;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by said on 06.05.17.
 */
public class TableUtilsTest {
    @Test
    public void createTable() throws Exception {
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ConnectionSource connectionSource = createConnectionSource(connection, statement);

        TableUtils.createTable(connectionSource, Foo.class);
        verify(statement, times(1)).execute("CREATE TABLE foo(id INTEGER PRIMARY KEY AUTOINCREMENT, test_name varchar(255)  NULL, foo1_id INT  NOT NULL, FOREIGN KEY (foo1_id) REFERENCES foo1(id));");
    }

    @Test
    public void isExistTable() throws Exception {
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ConnectionSource connectionSource = createConnectionSource(connection, statement);
        ResultSet resultSet1 = mock(ResultSet.class);
        ResultSet resultSet2 = mock(ResultSet.class);
        String sqlExist = "SELECT name FROM sqlite_master WHERE type='table' AND name='foo'";
        String sqlNotExist = "SELECT name FROM sqlite_master WHERE type='table' AND name='foo1'";

        when(resultSet1.getString("name")).thenReturn("foo");
        when(resultSet1.next()).thenReturn(true).thenReturn(false);
        when(statement.executeQuery(sqlExist)).thenReturn(resultSet1);

        when(resultSet2.getString("name")).thenReturn("");
        when(resultSet2.next()).thenReturn(true).thenReturn(false);
        when(statement.executeQuery(sqlNotExist)).thenReturn(resultSet2);

        Assert.assertEquals(true, TableUtils.isExistTable(connectionSource, "foo"));
        verify(resultSet1, times(1)).close();
        Assert.assertEquals(false, TableUtils.isExistTable(connectionSource, "foo1"));
        verify(resultSet2, times(1)).close();
        verify(connectionSource, times(2)).releaseConnection(connection);
        verify(statement, times(2)).close();
    }

    @Test
    public void createManyToManyTable() throws Exception {
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ConnectionSource connectionSource = createConnectionSource(connection, statement);
        TableInfo<Foo> tableInfo = new TableInfo<>(Foo.class);
        TableInfo<Foo3> tableInfo3 = new TableInfo<>(Foo3.class);

        String manyToManyTableName1 = tableInfo.getTableName() + "_" + tableInfo3.getTableName();
        String manyToManyTableName2 =  tableInfo3.getTableName() + "_"  + tableInfo.getTableName();

        ResultSet resultSet1 = mock(ResultSet.class);
        String sqlExist1 = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + manyToManyTableName1 + "'";

        when(resultSet1.getString("name")).thenReturn("");
        when(resultSet1.next()).thenReturn(true).thenReturn(false);
        when(statement.executeQuery(sqlExist1)).thenReturn(resultSet1);

        ResultSet resultSet3 = mock(ResultSet.class);
        String sqlExist2 = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + manyToManyTableName2 + "'";

        when(resultSet3.getString("name")).thenReturn("");
        when(resultSet3.next()).thenReturn(true).thenReturn(false);
        when(statement.executeQuery(sqlExist2)).thenReturn(resultSet3);

        TableUtils.createManyToManyTable(connectionSource, tableInfo, tableInfo3);
        String sql = "CREATE TABLE " + manyToManyTableName1 + "(" + tableInfo.getTableName() + "_id INTEGER NOT NULL, " + tableInfo3.getTableName() + "_id INTEGER NOT NULL)";

        verify(statement, times(1)).execute(sql);

        verify(connectionSource, times(3)).releaseConnection(connection);
        verify(statement, times(3)).close();

        verify(resultSet1, times(1)).close();
        verify(resultSet3, times(1)).close();
    }

    @Test
    public void getManyToManyRelationTableName() throws Exception {
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ConnectionSource connectionSource = createConnectionSource(connection, statement);
        ResultSet resultSet1 = mock(ResultSet.class);
        ResultSet resultSet2 = mock(ResultSet.class);
        String sqlExist = "SELECT name FROM sqlite_master WHERE type='table' AND name='foo_foo3'";
        String sqlNotExist = "SELECT name FROM sqlite_master WHERE type='table' AND name='foo3_foo'";

        when(resultSet1.getString("name")).thenReturn("foo_foo3");
        when(resultSet1.next()).thenReturn(true).thenReturn(false);
        when(statement.executeQuery(sqlExist)).thenReturn(resultSet1);

        when(resultSet2.getString("name")).thenReturn("");
        when(resultSet2.next()).thenReturn(true).thenReturn(false);
        when(statement.executeQuery(sqlNotExist)).thenReturn(resultSet2);

        Assert.assertEquals("foo_foo3", TableUtils.getManyToManyRelationTableName(connectionSource, "foo", "foo3"));
    }

    @Test(expected = SQLException.class)
    public void tableManyToManyDoesNotExist() throws Exception {
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ConnectionSource connectionSource = createConnectionSource(connection, statement);
        ResultSet resultSet1 = mock(ResultSet.class);
        ResultSet resultSet2 = mock(ResultSet.class);
        String sqlExist = "SELECT name FROM sqlite_master WHERE type='table' AND name='foo_foo3'";
        String sqlNotExist = "SELECT name FROM sqlite_master WHERE type='table' AND name='foo3_foo'";

        when(resultSet1.getString("name")).thenReturn("");
        when(resultSet1.next()).thenReturn(true).thenReturn(false);
        when(statement.executeQuery(sqlExist)).thenReturn(resultSet1);

        when(resultSet2.getString("name")).thenReturn("");
        when(resultSet2.next()).thenReturn(true).thenReturn(false);
        when(statement.executeQuery(sqlNotExist)).thenReturn(resultSet2);

        TableUtils.getManyToManyRelationTableName(connectionSource, "foo", "foo3");
    }

    @Test
    public void dropTable() throws Exception {
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ConnectionSource connectionSource = createConnectionSource(connection, statement);

        TableUtils.dropTable(connectionSource, Foo.class);
        verify(statement, times(1)).execute("DROP TABLE foo");
    }

    @Test
    public void clearTable() throws Exception {
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ConnectionSource connectionSource = createConnectionSource(connection, statement);

        TableUtils.clearTable(connectionSource, Foo.class);
        verify(statement, times(1)).execute("DELETE FROM foo");
    }

    private ConnectionSource createConnectionSource(Connection connection, Statement statement) throws SQLException {
        ConnectionSource connectionSource = mock(ConnectionSource.class);

        when(statement.execute(anyString())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return null;
            }
        });
        when(connectionSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);

        return connectionSource;
    }

}