package ru.saidgadjiev.orm.next.core.dao;

import ru.saidgadjiev.orm.next.core.cache.CacheContext;
import ru.saidgadjiev.orm.next.core.cache.ObjectCache;
import ru.saidgadjiev.orm.next.core.stament_executor.*;
import ru.saidgadjiev.orm.next.core.stament_executor.object.ObjectBuilder;
import ru.saidgadjiev.orm.next.core.stament_executor.object.ObjectCreator;
import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.CachedResultsMapperDecorator;
import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.ResultsMapper;
import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.ResultsMapperImpl;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Базовый класс для DAO. Используется в DaoBuilder
 *
 * @param <T>
 * @param <ID>
 */
public abstract class BaseDaoImpl<T, ID> implements Dao<T, ID> {

    private final ConnectionSource dataSource;

    private final TableInfo<T> tableInfo;

    private IStatementExecutor<T, ID> statementExecutor;

    private CacheContext cacheContext = new CacheContext();

    protected BaseDaoImpl(ConnectionSource dataSource, TableInfo<T> tableInfo) {
        this.tableInfo = tableInfo;
        this.dataSource = dataSource;
        this.statementExecutor = new StatementValidator<>(
                tableInfo,
                new CachedStatementExecutor<>(
                        tableInfo,
                        cacheContext,
                        new StatementExecutorImpl<>(
                                tableInfo,
                                new ObjectCreator<>(dataSource, tableInfo),
                                dataSource.getDatabaseType(),
                                new CachedResultsMapperDecorator<>(
                                        tableInfo,
                                        cacheContext,
                                        new ResultsMapperImpl<>(tableInfo, new ObjectBuilder<>(dataSource, tableInfo))
                                )
                        )
                )
        );
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
    public void caching(boolean flag) {
        cacheContext.caching(flag);
    }

    @Override
    public void setObjectCache(ObjectCache objectCache) {
        cacheContext.getObjectCache().ifPresent(ObjectCache::invalidateAll);
        if (objectCache != null) {
            cacheContext
                    .caching(true)
                    .objectCache(objectCache);
            objectCache.registerClass(tableInfo.getTableClass());
        }
    }

    @Override
    public void caching(boolean flag, ObjectCache objectCache) {
        cacheContext.objectCache(objectCache).caching(flag);

        if (objectCache != null) {
            cacheContext.getObjectCache().ifPresent(ObjectCache::invalidateAll);
            cacheContext.objectCache(objectCache);
            objectCache.registerClass(tableInfo.getTableClass());
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

    public static <T, ID> Dao<T, ID> createDao(ConnectionSource dataSource, TableInfo<T> tableInfoBuilder) {
        return new BaseDaoImpl<T, ID>(dataSource, tableInfoBuilder) {
        };
    }

    @Override
    public ConnectionSource getDataSource() {
        return dataSource;
    }

    @Override
    public TransactionImpl<T, ID> transaction() throws SQLException {
        Connection connection = dataSource.getConnection();

        return new TransactionImpl<>(statementExecutor, connection, () -> {
            dataSource.releaseConnection(connection);

            return null;
        });
    }
}
