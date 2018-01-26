package ru.said.orm.next.core.stament_executor;

import org.junit.Test;
import org.mockito.Mockito;
import ru.said.orm.next.core.stament_executor.object.DataBaseObject;
import ru.said.orm.next.core.support.ConnectionSource;

public class StatementExecutorImplTest {

    @Test
    public void create() {
        ConnectionSource connectionSource = Mockito.mock(ConnectionSource.class);
        DataBaseObject dataBaseObject = new DataBaseObject(null, null);
        StatementExecutorImpl statementExecutor = new StatementExecutorImpl(dataBaseObject);
    }

    @Test
    public void createTable() {
    }

    @Test
    public void dropTable() {
    }

    @Test
    public void update() {
    }

    @Test
    public void delete() {
    }

    @Test
    public void deleteById() {
    }

    @Test
    public void queryForId() {
    }

    @Test
    public void queryForAll() {
    }

    @Test
    public void createIndexes() {
    }

    @Test
    public void dropIndexes() {
    }

    @Test
    public void query() {
    }

    @Test
    public void countOff() {
    }
}