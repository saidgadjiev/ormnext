package utils;

import com.sun.tools.corba.se.idl.constExpr.Times;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import support.JDBCConnectionSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by said on 01.05.17.
 */
public class StatementExecutorTest {
    @Test
    public void execute() throws Exception {
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        JDBCConnectionSource connectionSource = createConnectionSource(connection, statement);
        String sql = "SELECT * FROM test";

        StatementExecutor.execute(connectionSource, sql);
        verify(connectionSource, times(1)).getConnection();
        verify(connectionSource, times(1)).releaseConnection(connection);
        verify(statement, times(1)).close();
        verify(statement, times(1)).execute(sql);
    }

    @Test
    public void queryForId() throws Exception {

    }

    @Test
    public void fillManyToMany() throws Exception {

    }

    @Test
    public void create() throws Exception {

    }

    @Test
    public void getIdValue() throws Exception {

    }

    private JDBCConnectionSource createConnectionSource(Connection connection, Statement statement) throws SQLException {
        JDBCConnectionSource connectionSource = mock(JDBCConnectionSource.class);

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