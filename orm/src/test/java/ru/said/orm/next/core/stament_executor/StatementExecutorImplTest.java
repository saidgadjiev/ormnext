package ru.said.orm.next.core.stament_executor;

import org.junit.Test;
import ru.said.orm.next.core.stament_executor.object.DataBaseObject;

public class StatementExecutorImplTest {

    @Test
    public void create() {
        DataBaseObject dataBaseObject = new DataBaseObject(null, null);
        StatementExecutorImpl statementExecutor = new StatementExecutorImpl(dataBaseObject);
    }
}