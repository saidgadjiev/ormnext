package ru.saidgadjiev.orm.next.core.dao;

import ru.saidgadjiev.orm.next.core.cache.CacheContext;
import ru.saidgadjiev.orm.next.core.criteria.impl.SelectStatement;
import ru.saidgadjiev.orm.next.core.stament_executor.*;
import ru.saidgadjiev.orm.next.core.stament_executor.object.operation.ForeignCreator;
import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.CachedResultsMapperDecorator;
import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.ResultsMapper;
import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.ResultsMapperImpl;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by said on 19.02.2018.
 */
public class SessionImpl implements Session {

    private final ConnectionSource dataSource;

    private IStatementExecutor statementExecutor;

    SessionImpl(ConnectionSource dataSource, CacheContext cacheContext) {
        this.dataSource = dataSource;
        Function<TableInfo<?>, ResultsMapper<?>> resultsMapperFactory = tableInfo -> new CachedResultsMapperDecorator<>(
                tableInfo,
                cacheContext,
                new ResultsMapperImpl<>(dataSource, tableInfo, tableInfo.getFieldTypes(), new HashSet<>())
        );

        this.statementExecutor = new StatementValidator(
                new CachedStatementExecutor(
                        cacheContext,
                        new StatementExecutorImpl(
                                dataSource.getDatabaseType(),
                                resultsMapperFactory,
                                new ForeignCreator<>(dataSource)
                        )
                )
        );
    }

    @Override
    public<T> int create(Collection<T> objects) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.create(connection, objects);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public<T> int create(T object) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.create(connection, object);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public<T> boolean createTable(Class<T> tClass, boolean ifNotExist) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.createTable(connection, tClass, ifNotExist);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public<T, ID> T queryForId(Class<T> tClass, ID id) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.queryForId(connection, tClass, id);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public<T> List<T> queryForAll(Class<T> tClass) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.queryForAll(connection, tClass);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public<T> int update(T object) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.update(connection, object);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public<T> int delete(T object) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.delete(connection, object);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    public<T, ID> int deleteById(Class<T> tClass, ID id) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.deleteById(connection, tClass, id);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public<T> boolean dropTable(Class<T> tClass, boolean ifExists) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.dropTable(connection, tClass, ifExists);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public<T> void createIndexes(Class<T> tClass) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            statementExecutor.createIndexes(connection, tClass);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public<T> void dropIndexes(Class<T> tClass) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            statementExecutor.dropIndexes(connection, tClass);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public<T> long countOff(Class<T> tClass) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.countOff(connection, tClass);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public long queryForLong(String query) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.queryForLong(connection, query);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public <R> GenericResults<R> query(String query) throws SQLException {
        return statementExecutor.query(dataSource, null, null, query);
    }

    @Override
    public <R> GenericResults<R> query(Class<R> resultClass, String query) throws SQLException {
        return statementExecutor.query(dataSource, resultClass, null, query);

    }

    @Override
    public <R> GenericResults<R> query(String query, Map<Integer, Object> args) throws SQLException {
        return statementExecutor.query(dataSource, null, args, query);
    }

    @Override
    public <R> GenericResults<R> query(Class<R> resultClass, Map<Integer, Object> args, String query) throws SQLException {
        return statementExecutor.query(dataSource, resultClass, args, query);
    }

    @Override
    public<R> GenericResults<R> query(SelectStatement<R> statement) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.query(dataSource, statement);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public TransactionImpl transaction() throws SQLException {
        return new TransactionImpl(statementExecutor, dataSource);
    }
}
