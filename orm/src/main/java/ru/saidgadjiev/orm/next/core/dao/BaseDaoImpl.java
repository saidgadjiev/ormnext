package ru.saidgadjiev.orm.next.core.dao;

import ru.saidgadjiev.orm.next.core.cache.CacheContext;
import ru.saidgadjiev.orm.next.core.cache.ObjectCache;
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
import java.util.Optional;
import java.util.function.Function;

/**
 * Базовый класс для DAO. Используется в DaoBuilder
 */
public abstract class BaseDaoImpl implements Dao {

    private final ConnectionSource dataSource;

    private IStatementExecutor statementExecutor;

    private CacheContext cacheContext = new CacheContext();

    protected BaseDaoImpl(ConnectionSource dataSource) {
        this.dataSource = dataSource;
        Function<TableInfo<?>, ObjectBuilder<?>> objectBuilderFactory = (tableInfo) -> new ObjectBuilder<>(dataSource, tableInfo);
        Function<TableInfo<?>, CreateQueryBuilder<?>> objectCreatorFactory = CreateQueryBuilder::new;
        Function<TableInfo<?>, ResultsMapper<?>> resultsMapperFactory = (tableInfo) -> new CachedResultsMapperDecorator<>(
                cacheContext,
                new ResultsMapperImpl(tableInfo, objectBuilderFactory.apply(tableInfo))
        );

        this.statementExecutor = new StatementValidator(
                new CachedStatementExecutor(
                        cacheContext,
                        new StatementExecutorImpl(
                                objectCreatorFactory,
                                dataSource.getDatabaseType(),
                                resultsMapperFactory,
                                new ForeignCreator(dataSource)
                        )
                )
        );
    }

    @Override
    public <T> int create(Collection<T> objects) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.create(connection, objects);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public <T> int create(T object) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.create(connection, object);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public <T> boolean createTable(Class<T> tClass, boolean ifNotExist) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.createTable(connection, tClass, ifNotExist);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public <T, ID> T queryForId(ID id, Class<T> tClass) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.queryForId(connection, tClass, id);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public <T> List<T> queryForAll(Class<T> tClass) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.queryForAll(connection, tClass);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public <T> int update(T object) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.update(connection, object);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public <T> int delete(T object) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.delete(connection, object);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public <T, ID> int deleteById(ID id, Class<T> tClass) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.deleteById(connection, tClass, id);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public void caching(boolean flag, Class<?>... classes) {
        Optional<ObjectCache> objectCache = cacheContext.getObjectCache();

        objectCache.ifPresent(objectCache1 -> {
            for (Class<?> clazz : classes) {
                cacheContext.caching(clazz, flag);
                objectCache1.registerClass(clazz);
            }
        });

    }

    @Override
    public void setObjectCache(ObjectCache objectCache, Class<?>... classes) {
        cacheContext.getObjectCache().ifPresent(ObjectCache::invalidateAll);
        if (objectCache != null) {
            cacheContext
                    .objectCache(objectCache);

            for (Class<?> clazz : classes) {
                cacheContext.caching(clazz, true);
                objectCache.registerClass(clazz);
            }
        }
    }

    @Override
    public <T> boolean dropTable(Class<T> tClass, boolean ifExists) throws SQLException {
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
    public <R> GenericResults<R> query(String query, ResultsMapper<R> resultsMapper) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return statementExecutor.query(connection, query, resultsMapper);
        }
    }

    public static Dao createDao(ConnectionSource dataSource) {
        return new BaseDaoImpl(dataSource) {};
    }

    @Override
    public ConnectionSource getDataSource() {
        return dataSource;
    }

    @Override
    public TransactionImpl transaction() throws SQLException {
        Connection connection = dataSource.getConnection();

        return new TransactionImpl(statementExecutor, connection, () -> {
            dataSource.releaseConnection(connection);

            return null;
        });
    }
}
