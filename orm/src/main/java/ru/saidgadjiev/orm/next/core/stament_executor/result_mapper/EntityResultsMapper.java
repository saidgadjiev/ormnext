package ru.saidgadjiev.orm.next.core.stament_executor.result_mapper;

import ru.saidgadjiev.orm.next.core.stament_executor.DatabaseResults;

public interface EntityResultsMapper<T> {

    T mapResults(DatabaseResults results, Class<T> resultClass) throws Exception;
}
