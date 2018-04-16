package ru.saidgadjiev.orm.next.core.table.persister;

import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.ResultsMapper;
import ru.saidgadjiev.orm.next.core.table.DatabaseEntityMetadata;
import ru.saidgadjiev.orm.next.core.table.persister.instatiator.Instantiator;

import java.lang.reflect.Constructor;

public class DatabaseEntityPersisterImpl implements DatabaseEntityPersister {

    private QueryDetails queryDetails;

    private DatabaseEntityMetadata entityMetadata;

    private Instantiator instantiator;

    public DatabaseEntityPersisterImpl(QueryDetails queryDetails) {
        this.queryDetails = queryDetails;
    }

    public Object readRow() {
        return null;
    }

    public Object instantiate() {
        return instantiator.instantiate();
    }
}
