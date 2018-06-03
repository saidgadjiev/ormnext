package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.cache.CacheHolder;
import ru.saidgadjiev.ormnext.core.connectionsource.ConnectionSource;
import ru.saidgadjiev.ormnext.core.connectionsource.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.connectionsource.DatabaseResults;
import ru.saidgadjiev.ormnext.core.dao.transaction.state.BeginState;
import ru.saidgadjiev.ormnext.core.dao.transaction.state.InternalTransaction;
import ru.saidgadjiev.ormnext.core.dao.transaction.state.TransactionState;
import ru.saidgadjiev.ormnext.core.loader.BatchEntityLoader;
import ru.saidgadjiev.ormnext.core.loader.CacheHelper;
import ru.saidgadjiev.ormnext.core.loader.DefaultEntityLoader;
import ru.saidgadjiev.ormnext.core.loader.EntityLoader;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private EntityLoader entityLoader;

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

        this.cacheHelper = new CacheHelper(cacheHolder, sessionManager.getMetaModel());
        this.entityLoader = new DefaultEntityLoader(
                this,
                sessionManager.getMetaModel(),
                sessionManager.getDatabaseEngine()
        );
    }

    @Override
    public int create(Object object) throws SQLException {
        return entityLoader.create(connection, object);
    }

    @Override
    public int create(Object[] object) throws SQLException {
        return entityLoader.create(connection, object);
    }

    @Override
    public boolean createTable(Class<?> tClass, boolean ifNotExist) throws SQLException {
        return entityLoader.createTable(connection, tClass, ifNotExist);
    }

    @Override
    public Map<Class<?>, Boolean> createTables(Class<?>[] classes, boolean ifNotExist) throws SQLException {
        Map<Class<?>, Boolean> result = new HashMap<>();

        for (Class<?> tableClass : classes) {
            result.put(tableClass, createTable(tableClass, ifNotExist));
        }

        return result;
    }

    @Override
    public <T> T queryForId(Class<T> tClass, Object id) throws SQLException {
        return entityLoader.queryForId(connection, tClass, id);
    }

    @Override
    public <T> List<T> queryForAll(Class<T> tClass) throws SQLException {
        return entityLoader.queryForAll(connection, tClass);
    }

    @Override
    public int update(Object object) throws SQLException {
        return entityLoader.update(connection, object);
    }

    @Override
    public int delete(Object object) throws SQLException {
        return entityLoader.delete(connection, object);
    }

    @Override
    public int deleteById(Class<?> tClass, Object id) throws SQLException {
        return entityLoader.deleteById(connection, tClass, id);
    }

    @Override
    public boolean refresh(Object object) throws SQLException {
        return entityLoader.refresh(connection, object);
    }

    @Override
    public Map<Class<?>, Boolean> dropTables(Class<?>[] classes, boolean ifExist) throws SQLException {
        Map<Class<?>, Boolean> result = new HashMap<>();

        for (Class<?> tableClass : classes) {
            result.put(tableClass, dropTable(tableClass, ifExist));
        }

        return result;
    }

    @Override
    public boolean dropTable(Class<?> tClass, boolean ifExist) throws SQLException {
        return entityLoader.dropTable(connection, tClass, ifExist);
    }

    @Override
    public void createIndexes(Class<?> tClass) throws SQLException {
        entityLoader.createIndexes(connection, tClass);
    }

    @Override
    public void dropIndexes(Class<?> tClass) throws SQLException {
        entityLoader.dropIndexes(connection, tClass);
    }

    @Override
    public long countOff(Class<?> tClass) throws SQLException {
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
    public long queryForLong(SelectStatement<?> selectStatement) throws SQLException {
        return entityLoader.queryForLong(connection, selectStatement);
    }

    @Override
    public DatabaseResults query(String query) throws SQLException {
        return entityLoader.query(connection, query);
    }

    @Override
    public void batch() {
        entityLoader = new BatchEntityLoader(
                this,
                sessionManager.getMetaModel(),
                sessionManager.getDatabaseEngine()
        );

        entityLoader.batch();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        int[] batchResults = entityLoader.executeBatch(connection);

        entityLoader = new DefaultEntityLoader(
                this,
                sessionManager.getMetaModel(),
                sessionManager.getDatabaseEngine()
        );

        return batchResults;
    }

    @Override
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    @Override
    public void close() throws SQLException {
        connectionSource.releaseConnection((DatabaseConnection) connection);
    }
}
