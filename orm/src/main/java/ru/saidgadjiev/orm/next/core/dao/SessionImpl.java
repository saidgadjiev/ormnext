package ru.saidgadjiev.orm.next.core.dao;

import ru.saidgadjiev.orm.next.core.cache.CacheContext;
import ru.saidgadjiev.orm.next.core.criteria.impl.SelectStatement;
import ru.saidgadjiev.orm.next.core.stament_executor.*;
import ru.saidgadjiev.orm.next.core.stament_executor.object.operation.ForeignCreator;
import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.CachedResultsMapperDecorator;
import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.ResultsMapperImpl;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by said on 19.02.2018.
 */
public class SessionImpl<T, ID> implements Session<T, ID> {

    private final ConnectionSource dataSource;

    private IStatementExecutor<T, ID> statementExecutor;

    SessionImpl(ConnectionSource dataSource, CacheContext cacheContext, TableInfo<T> tableInfo) {
        this.dataSource = dataSource;
        this.statementExecutor = new StatementValidator<>(
                tableInfo,
                new CachedStatementExecutor<>(
                        tableInfo,
                        cacheContext,
                        new StatementExecutorImpl<>(
                                tableInfo,
                                dataSource.getDatabaseType(),
                                new CachedResultsMapperDecorator<>(
                                        tableInfo,
                                        cacheContext,
                                        new ResultsMapperImpl<>(dataSource, tableInfo, tableInfo.getFieldTypes(), new HashSet<>())
                                ),
                                new ForeignCreator<>(tableInfo, dataSource)
                        )
                )
        );
    }

    @Override
    public int create(Collection<T> objects) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.create(connection, objects);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public int create(T object) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.create(connection, object);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public boolean createTable(boolean ifNotExist) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.createTable(connection, ifNotExist);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public T queryForId(ID id) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.queryForId(connection, id);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public List<T> queryForAll() throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.queryForAll(connection);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public int update(T object) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.update(connection, object);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public int delete(T object) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.delete(connection, object);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public int deleteById(ID id) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.deleteById(connection, id);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public boolean dropTable(boolean ifExists) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.dropTable(connection, ifExists);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public void createIndexes() throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            statementExecutor.createIndexes(connection);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public void dropIndexes() throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            statementExecutor.dropIndexes(connection);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public long countOff() throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.countOff(connection);
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
        return statementExecutor.query(dataSource, query, null);

    }

    @Override
    public <R> GenericResults<R> query(String query, Map<Integer, Object> args) throws SQLException {
        return statementExecutor.query(dataSource, query, args);
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
    public TransactionImpl<T, ID> transaction() throws SQLException {
        return new TransactionImpl<>(statementExecutor, dataSource);
    }
}
