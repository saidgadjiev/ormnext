package ru.saidgadjiev.orm.next.core.stamentexecutor.resultmapper;

import ru.saidgadjiev.orm.next.core.stamentexecutor.DatabaseResults;

public interface ResultsMapper<T> {

    T mapResults(DatabaseResults results) throws Exception;
}
