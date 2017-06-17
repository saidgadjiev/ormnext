package dao;

import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by said on 01.05.17.
 */
public class DaoImplTest {
    @Test
    public void create() throws Exception {

    }

    @Test
    public void queryForId() throws Exception {

    }

    @Test
    public void queryForAll() throws Exception {

    }

    @Test
    public void queryForAll1() throws Exception {

    }

    @Test
    public void update() throws Exception {

    }

    @Test
    public void delete() throws Exception {

    }

    @Test
    public void queryForWhere() throws Exception {

    }

    @Test
    public void queryForUpdate() throws Exception {

    }

    @Test
    public void deleteForWhere() throws Exception {

    }

    @Test
    public void queryBuilder() throws Exception {

    }

    private ConnectionSource createConnectionSource() throws SQLException {
        Connection connection = mock(Connection.class);
        ConnectionSource connectionSource = mock(ConnectionSource.class);
        Statement statement = mock(Statement.class);

        when(connectionSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);

        return connectionSource;
    }

}