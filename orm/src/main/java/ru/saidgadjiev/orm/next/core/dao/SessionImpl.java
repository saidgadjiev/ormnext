package ru.saidgadjiev.orm.next.core.dao;

import ru.saidgadjiev.orm.next.core.cache.CacheContext;
import ru.saidgadjiev.orm.next.core.cache.ObjectCache;
import ru.saidgadjiev.orm.next.core.criteria.impl.SelectStatement;
import ru.saidgadjiev.orm.next.core.stamentexecutor.GenericResults;
import ru.saidgadjiev.orm.next.core.stamentexecutor.IStatementExecutor;
import ru.saidgadjiev.orm.next.core.stamentexecutor.StatementExecutorImpl;
import ru.saidgadjiev.orm.next.core.stamentexecutor.object.operation.ForeignCreator;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by said on 19.02.2018.
 */
public class SessionImpl implements Session {

    private final ConnectionSource dataSource;

    private IStatementExecutor statementExecutor;

    private ObjectCache sessionCache;

    SessionImpl(SessionManager sessionManager, CacheContext cacheContext) {
        this.dataSource = sessionManager.getDataSource();
        this.sessionCache = new SessionObjectCache();
        CacheContext sessionCacheContext = new CacheContext(sessionCache);

        sessionManager.getMetaModel().getPersistentClasses().forEach(clazz -> sessionCache.registerClass(clazz));
        sessionCacheContext.caching(sessionManager.getMetaModel().getPersistentClasses(), true);
        this.statementExecutor = new StatementExecutorImpl(
                this,
                sessionManager.getMetaModel(),
                dataSource.getDatabaseType(),
                new ForeignCreator<>(this)
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
    public <T, ID> T queryForId(Class<T> tClass, ID id) throws SQLException {
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

    public <T, ID> int deleteById(Class<T> tClass, ID id) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.deleteById(connection, tClass, id);
        } finally {
            dataSource.releaseConnection(connection);
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
    public <T> void createIndexes(Class<T> tClass) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            statementExecutor.createIndexes(connection, tClass);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public <T> void dropIndexes(Class<T> tClass) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            statementExecutor.dropIndexes(connection, tClass);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public <T> long countOff(Class<T> tClass) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.countOff(connection, tClass);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public <R> GenericResults<R> query(SelectStatement<R> statement) throws SQLException {
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

    @Override
    public void addEntityToCache(Class<?> clazz, Object id, Object data) {
        sessionCache.put(clazz, id, data);
    }

    @Override
    public void close() {
        sessionCache.invalidateAll();
    }

    private static final class SessionObjectCache implements ObjectCache {

        private Map<Class<?>, Map<Object, Object>> cache = new HashMap<>();

        @Override
        public void registerClass(Class<?> tClass) {
            cache.computeIfAbsent(tClass, aClass -> new HashMap<>());
        }

        @Override
        public void put(Class<?> tClass, Object id, Object data) {
            Map<Object, Object> objectCache = cache.get(tClass);

            if (objectCache != null) {
                objectCache.put(id, data);
            }
        }

        @Override
        public Object get(Class<?> tClass, Object id) {
            Map<Object, Object> objectCache = cache.get(tClass);

            if (objectCache == null) {
                return null;
            }

            return objectCache.get(id);
        }

        @Override
        public boolean contains(Class<?> tClass, Object id) {
            Map<Object, Object> objectCache = cache.get(tClass);

            return objectCache != null && objectCache.containsKey(id);
        }

        @Override
        public void invalidate(Class<?> tClass, Object id) {
            Map<Object, Object> objectCache = cache.get(tClass);

            if (objectCache == null) {
                return;
            }
            objectCache.remove(id);
        }

        @Override
        public void invalidateAll(Class<?> tClass) {
            Map<Object, Object> objectCache = cache.get(tClass);

            if (objectCache == null) {
                return;
            }
            objectCache.clear();
        }

        @Override
        public void invalidateAll() {
            cache.forEach((key, value) -> value.clear());
        }

        @Override
        public long size(Class<?> tClass) {
            Map<Object, Object> objectCache = cache.get(tClass);

            if (objectCache == null) {
                return 0;
            }

            return objectCache.size();
        }

        @Override
        public long sizeAll() {
            long count = 0;

            for (Map<Object, Object> cache : cache.values()) {
                count += cache.size();
            }

            return count;
        }
    }
}
