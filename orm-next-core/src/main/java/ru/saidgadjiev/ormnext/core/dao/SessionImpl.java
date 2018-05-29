package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.cache.CacheHolder;
import ru.saidgadjiev.ormnext.core.cache.ObjectCache;
import ru.saidgadjiev.ormnext.core.cache.ReferenceObjectCache;
import ru.saidgadjiev.ormnext.core.connection_source.ConnectionSource;
import ru.saidgadjiev.ormnext.core.connection_source.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.connection_source.DatabaseResults;
import ru.saidgadjiev.ormnext.core.dao.transaction.state.BeginState;
import ru.saidgadjiev.ormnext.core.dao.transaction.state.InternalTransaction;
import ru.saidgadjiev.ormnext.core.dao.transaction.state.TransactionState;
import ru.saidgadjiev.ormnext.core.loader.CacheHelper;
import ru.saidgadjiev.ormnext.core.loader.DefaultEntityLoader;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;

import java.sql.SQLException;
import java.util.List;

/**
 * Concrete session implementation {@link Session}.
 *
 * @author said gadjiev
 */
public class SessionImpl implements Session, InternalTransaction {

    /**
     * Entity loader.
     *
     * @see DefaultEntityLoader
     */
    private final DefaultEntityLoader entityLoader;

    /**
     * Cache helper.
     *
     * @see CacheHelper
     */
    private final CacheHelper cacheHelper;

    /**
     * Connection source.
     *
     * @see ConnectionSource
     */
    private final ConnectionSource<?> connectionSource;

    /**
     * Session manager which create this session instance.
     */
    private final SessionManager sessionManager;

    /**
     * Current database connection.
     */
    private final DatabaseConnection<?> connection;

    /**
     * Session cache.
     *
     * @see CacheHolder
     */
    private final CacheHolder cacheHolder;

    /**
     * Transaction state.
     *
     * @see TransactionState
     */
    private TransactionState transactionState;

    /**
     * Create new session instance.
     *
     * @param connectionSource target connection source
     * @param connection       target database connection
     * @param cacheHolder      object cache
     * @param sessionManager   session manager which create this instance
     */
    SessionImpl(ConnectionSource<?> connectionSource,
                DatabaseConnection<?> connection,
                CacheHolder cacheHolder,
                SessionManager sessionManager) {
        this.connectionSource = connectionSource;
        this.sessionManager = sessionManager;
        this.connection = connection;
        ObjectCache sessionCache = new ReferenceObjectCache<>();
        this.cacheHolder = new CacheHolder().objectCache(sessionCache);

        sessionManager.getMetaModel()
                .getPersistentClasses()
                .forEach(sessionCache::registerClass);

        this.cacheHelper = new CacheHelper(cacheHolder, this.cacheHolder);
        this.entityLoader = new DefaultEntityLoader(
                this,
                sessionManager.getMetaModel(),
                sessionManager.getDatabaseEngine()
        );
    }

    @Override
    public <T> void create(T object) throws SQLException {
        entityLoader.create(connection, object);
    }

    @Override
    public <T> boolean createTable(Class<T> tClass, boolean ifNotExist) throws SQLException {
        return entityLoader.createTable(connection, tClass, ifNotExist);
    }

    @Override
    public <T, ID> T queryForId(Class<T> tClass, ID id) throws SQLException {
        return entityLoader.queryForId(connection, tClass, id);
    }

    @Override
    public <T> List<T> queryForAll(Class<T> tClass) throws SQLException {
        return entityLoader.queryForAll(connection, tClass);
    }

    @Override
    public <T> void update(T object) throws SQLException {
        entityLoader.update(connection, object);
    }

    @Override
    public <T> void delete(T object) throws SQLException {
        entityLoader.delete(connection, object);
    }

    @Override
    public <T, ID> void deleteById(Class<T> tClass, ID id) throws SQLException {
        entityLoader.deleteById(connection, tClass, id);
    }

    @Override
    public <T> boolean dropTable(Class<T> tClass, boolean ifExist) throws SQLException {
        return entityLoader.dropTable(connection, tClass, ifExist);
    }

    @Override
    public <T> void createIndexes(Class<T> tClass) throws SQLException {
        entityLoader.createIndexes(connection, tClass);
    }

    @Override
    public <T> void dropIndexes(Class<T> tClass) throws SQLException {
        entityLoader.dropIndexes(connection, tClass);
    }

    @Override
    public <T> long countOff(Class<T> tClass) throws SQLException {
        return entityLoader.countOff(connection, tClass);
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
    public CacheHelper cacheHelper() {
        return cacheHelper;
    }

    @Override
    public <T> List<T> list(SelectStatement<T> criteria) throws SQLException {
        return entityLoader.list(connection, criteria);
    }

    @Override
    public <T> long queryForLong(SelectStatement<T> selectStatement) throws SQLException {
        return entityLoader.queryForLong(connection, selectStatement);
    }

    @Override
    public DatabaseResults query(String query) throws SQLException {
        return entityLoader.query(connection, query);
    }

    @Override
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    @Override
    public void close() throws SQLException {
        connectionSource.releaseConnection((DatabaseConnection) connection);
        cacheHolder.close();
    }
}
