package ru.saidgadjiev.orm.next.core.dao;

import ru.saidgadjiev.orm.next.core.cache.CacheContext;
import ru.saidgadjiev.orm.next.core.criteria.impl.DeleteStatement;
import ru.saidgadjiev.orm.next.core.criteria.impl.SelectStatement;
import ru.saidgadjiev.orm.next.core.criteria.impl.UpdateStatement;
import ru.saidgadjiev.orm.next.core.stament_executor.*;
import ru.saidgadjiev.orm.next.core.stament_executor.object.CreateQueryBuilder;
import ru.saidgadjiev.orm.next.core.stament_executor.object.ObjectBuilder;
import ru.saidgadjiev.orm.next.core.stament_executor.object.operation.ForeignCreator;
import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.CachedResultsMapperDecorator;
import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.ResultsMapper;
import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.ResultsMapperImpl;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by said on 19.02.2018.
 */
public class SessionImpl<T, ID> implements Session<T, ID> {

    private final ConnectionSource dataSource;

    private TableInfo<T> tableInfo;

    private IStatementExecutor<T, ID> statementExecutor;

    SessionImpl(ConnectionSource dataSource, CacheContext cacheContext, TableInfo<T> tableInfo) {
        this.dataSource = dataSource;
        this.tableInfo = tableInfo;
        Supplier<ObjectBuilder<T>> objectBuilderFactory = () -> new ObjectBuilder<>(dataSource, tableInfo);
        Supplier<CreateQueryBuilder<T>> objectCreatorFactory = () -> new CreateQueryBuilder<>(tableInfo);

        this.statementExecutor = new StatementValidator<>(
                tableInfo,
                new CachedStatementExecutor<>(
                        tableInfo,
                        cacheContext,
                        new StatementExecutorImpl<>(
                                tableInfo,
                                objectCreatorFactory,
                                dataSource.getDatabaseType(),
                                new CachedResultsMapperDecorator<>(
                                        tableInfo,
                                        cacheContext,
                                        new ResultsMapperImpl<>(tableInfo, objectBuilderFactory)
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
    public <R> GenericResults<R> query(String query, ResultsMapper<R> resultsMapper) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return statementExecutor.query(connection, query, resultsMapper);
        }
    }

    @Override
    public List<T> query(SelectStatement<T> statement) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return statementExecutor.query(connection, statement, null);
        }
    }

    @Override
    public List<T> query(SelectStatement<T> statement, ResultsMapper<T> resultsMapper) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return statementExecutor.query(connection, statement, resultsMapper);
        }
    }

    @Override
    public TransactionImpl<T, ID> transaction() throws SQLException {
        Connection connection = dataSource.getConnection();

        return new TransactionImpl<>(statementExecutor, connection, () -> {
            dataSource.releaseConnection(connection);

            return null;
        });
    }

    @Override
    public SelectStatement<T> selectQuery() {
        return new SelectStatement<>(tableInfo);
    }

    @Override
    public UpdateStatement<T> updateQuery() {
        return null;
    }

    @Override
    public DeleteStatement<T> deleteQuery() {
        return null;
    }
}
