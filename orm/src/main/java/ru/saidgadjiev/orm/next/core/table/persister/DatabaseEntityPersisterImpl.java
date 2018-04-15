package ru.saidgadjiev.orm.next.core.table.persister;

import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.ResultsMapper;

public class DatabaseEntityPersisterImpl implements DatabaseEntityPersister {

    private ResultsMapper resultsMapper;

    public DatabaseEntityPersisterImpl(ResultsMapper resultsMapper) {
        this.resultsMapper = resultsMapper;
    }
}
