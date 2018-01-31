package ru.said.orm.next.core.stament_executor.result_mapper;

import ru.said.orm.next.core.stament_executor.DatabaseResults;

public interface ResultsMapper<T> {

    T mapResults(DatabaseResults results) throws Exception;
}
