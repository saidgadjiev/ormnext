package ru.saidgadjiev.orm.next.core.stamentexecutor.resultmapper;

import ru.saidgadjiev.orm.next.core.stamentexecutor.DatabaseResults;
import ru.saidgadjiev.orm.next.core.table.persister.instatiator.Instantiator;

public class ResultsMapperImpl1<T> implements ResultsMapper<T> {

    private Instantiator instantiator;

    public ResultsMapperImpl1(Instantiator instantiator) {
        this.instantiator = instantiator;
    }

    @Override
    public T mapResults(DatabaseResults results) throws Exception {
       T object = (T) instantiator.instantiate();

       return object;
    }
}
