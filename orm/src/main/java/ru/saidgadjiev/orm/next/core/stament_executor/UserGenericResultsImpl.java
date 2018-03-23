package ru.saidgadjiev.orm.next.core.stament_executor;

import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.ResultsMapper;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserGenericResultsImpl<T> extends GenericResultsImpl<T> {

    public UserGenericResultsImpl(ConnectionSource connectionSource,
                                  Connection connection,
                                  IPreparedStatement statement) throws SQLException {
        super(connectionSource, connection, statement, null);
    }

    @Override
    public List<T> getResults() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public T getFirstResult() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> getResults(ResultsMapper<T> resultsMapper) throws SQLException {
        return null;
    }

    @Override
    public T getFirstResult(ResultsMapper<T> resultsMapper) throws SQLException {
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}
