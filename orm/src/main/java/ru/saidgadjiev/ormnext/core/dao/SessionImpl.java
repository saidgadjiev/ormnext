package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.cache.CacheContext;
import ru.saidgadjiev.ormnext.core.cache.ObjectCache;
import ru.saidgadjiev.ormnext.core.cache.ReferenceObjectCache;
import ru.saidgadjiev.ormnext.core.criteria.impl.CriteriaQuery;
import ru.saidgadjiev.ormnext.core.criteria.impl.SimpleCriteriaQuery;
import ru.saidgadjiev.ormnext.core.dao.transaction.BeginState;
import ru.saidgadjiev.ormnext.core.dao.transaction.TransactionState;
import ru.saidgadjiev.ormnext.core.stamentexecutor.CacheHelper;
import ru.saidgadjiev.ormnext.core.stamentexecutor.DefaultEntityLoader;
import ru.saidgadjiev.ormnext.core.stamentexecutor.object.OrmNextMethodHandler;
import ru.saidgadjiev.ormnext.core.support.ConnectionSource;
import ru.saidgadjiev.ormnext.core.support.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;
import ru.saidgadjiev.proxymaker.ProxyMaker;

import javax.transaction.TransactionRolledbackException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by said on 19.02.2018.
 */
public class SessionImpl implements InternalSession {

    private DefaultEntityLoader statementExecutor;

    private CacheHelper cacheHelper;

    private final ConnectionSource<Object> dataSource;

    private SessionManager sessionManager;

    private DatabaseConnection<Object> connection;

    private DatabaseEngine databaseEngine;

    private TransactionState transactionState;

    SessionImpl(ConnectionSource<Object> dataSource,
                DatabaseConnection<Object> connection,
                DatabaseEngine databaseEngine,
                MetaModel metaModel,
                CacheContext cacheContext,
                SessionManager sessionManager) {
        this.dataSource = dataSource;
        this.sessionManager = sessionManager;
        this.connection = connection;
        this.databaseEngine = databaseEngine;
        ObjectCache objectCache = new SessionObjectCache();

        metaModel.getPersistentClasses().forEach(objectCache::registerClass);
        this.cacheHelper = new CacheHelper(cacheContext, new CacheContext().objectCache(objectCache));
        this.statementExecutor = new DefaultEntityLoader(this, metaModel, databaseEngine);
    }

    @Override
    public <T> void create(Collection<T> objects) throws SQLException {
        statementExecutor.create(connection, objects);
    }

    @Override
    public <T> void create(T object) throws SQLException {
        statementExecutor.create(connection, object);
    }

    @Override
    public <T> boolean createTable(Class<T> tClass, boolean ifNotExist) throws SQLException {
        return statementExecutor.createTable(connection, tClass, ifNotExist);
    }

    @Override
    public <T, ID> T queryForId(Class<T> tClass, ID id) throws SQLException {
        return statementExecutor.queryForId(connection, tClass, id);
    }

    @Override
    public <T> List<T> queryForAll(Class<T> tClass) throws SQLException {
        return statementExecutor.queryForAll(connection, tClass);
    }

    @Override
    public <T> void update(T object) throws SQLException {
        statementExecutor.update(connection, object);
    }

    @Override
    public <T> void delete(T object) throws SQLException {
        statementExecutor.delete(connection, object);
    }

    public <T, ID> void deleteById(Class<T> tClass, ID id) throws SQLException {
        statementExecutor.deleteById(connection, tClass, id);
    }

    @Override
    public <T> boolean dropTable(Class<T> tClass, boolean ifExists) throws SQLException {
        return statementExecutor.dropTable(connection, tClass, ifExists);
    }

    @Override
    public <T> void createIndexes(Class<T> tClass) throws SQLException {
        statementExecutor.createIndexes(connection, tClass);
    }

    @Override
    public <T> void dropIndexes(Class<T> tClass) throws SQLException {
        statementExecutor.dropIndexes(connection, tClass);
    }

    @Override
    public <T> long countOff(Class<T> tClass) throws SQLException {
        return statementExecutor.countOff(connection, tClass);
    }

    @Override
    public void beginTransaction() throws SQLException {
        transactionState = new BeginState(this);

        transactionState.begin(connection);
    }

    @Override
    public void commit() throws SQLException {
        if (transactionState == null) {
            return;
        }
        transactionState.commit(connection);
    }

    @Override
    public void rollback() throws SQLException {
        if (transactionState == null) {
            return;
        }
        transactionState.rollback(connection);
    }

    @Override
    public void changeState(TransactionState transactionState) {
        this.transactionState = transactionState;
    }

    @Override
    public void close() throws SQLException {
        dataSource.releaseConnection(connection);
    }

    @Override
    public CacheHelper cacheHelper() {
        return cacheHelper;
    }

    @Override
    public <T> List<T> list(CriteriaQuery<T> criteria) throws SQLException {
        return statementExecutor.list(connection, criteria);
    }

    @Override
    public long queryForLong(SimpleCriteriaQuery simpleCriteriaQuery) throws SQLException {
        return statementExecutor.queryForLong(connection, simpleCriteriaQuery);
    }

    @Override
    public DatabaseEngine getDatabaseEngine() {
        return databaseEngine;
    }

    @Override
    public SessionManager getSessionManager() {
        return sessionManager;
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
