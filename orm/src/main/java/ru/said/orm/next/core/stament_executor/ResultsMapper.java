package ru.said.orm.next.core.stament_executor;

public interface ResultsMapper<T> {

    T mapResults(DatabaseResults results) throws Exception;
}
