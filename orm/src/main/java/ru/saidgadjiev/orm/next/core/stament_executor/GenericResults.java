package ru.saidgadjiev.orm.next.core.stament_executor;

import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.ResultsMapper;

import java.sql.SQLException;
import java.util.List;

public interface GenericResults<T> extends AutoCloseable {

    List<T> getResults() throws SQLException;

    T getFirstResult() throws SQLException;

    List<T> getResults(ResultsMapper<T> resultsMapper) throws SQLException;

    T getFirstResult(ResultsMapper<T> resultsMapper) throws SQLException;

    void close() throws SQLException;
}
