package ru.saidgadjiev.orm.next.core.stament_executor;

import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.ResultsMapper;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 10.03.2018.
 */
public class GenericResultsImpl<T> implements GenericResults<T> {

    private final ConnectionSource connectionSource;

    private final Connection connection;

    private IPreparedStatement statement;

    private DatabaseResults databaseResults;

    private ResultsMapper defaultMapper;

    public GenericResultsImpl(ConnectionSource connectionSource,
                              Connection connection,
                              IPreparedStatement statement,
                              ResultsMapper defaultMapper) throws SQLException {
        this.connectionSource = connectionSource;
        this.connection = connection;
        this.statement = statement;
        this.databaseResults = statement.executeQuery();
        this.defaultMapper = defaultMapper;
    }

    @Override
    public List<T> getResults() throws SQLException {
        return getResults(defaultMapper);
    }

    @Override
    public T getFirstResult() throws SQLException {
        return (T) getFirstResult(defaultMapper);
    }

    @Override
    public List<T> getResults(ResultsMapper<T> resultsMapper) throws SQLException {
        try {
            List<T> resultObjectList = new ArrayList<>();

            while (databaseResults.next()) {
                resultObjectList.add(resultsMapper.mapResults(databaseResults));
            }

            return resultObjectList;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public T getFirstResult(ResultsMapper<T> resultsMapper) throws SQLException {
        try {
            if (databaseResults.next()) {
                return resultsMapper.mapResults(databaseResults);
            }

            return null;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public void close() throws SQLException {
        databaseResults.close();
        statement.close();
        connectionSource.releaseConnection(connection);
    }

}
