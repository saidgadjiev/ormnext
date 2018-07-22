package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.connection.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.connection.source.ConnectionSource;
import ru.saidgadjiev.ormnext.core.dao.transaction.state.BeginState;
import ru.saidgadjiev.ormnext.core.dao.transaction.state.InternalTransaction;
import ru.saidgadjiev.ormnext.core.dao.transaction.state.TransactionState;
import ru.saidgadjiev.ormnext.core.loader.EntityLoader;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.DeleteStatement;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.UpdateStatement;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Concrete session implementation {@link Session}.
 *
 * @author Said Gadjiev
 */
public class SessionImpl implements Session, InternalTransaction {

    /**
     * Connection source.
     *
     * @see ConnectionSource
     */
    private final ConnectionSource<?> connectionSource;

    /**
     * Registered loaders.
     */
    private Map<EntityLoader.Loader, EntityLoader> loaders;
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
     * Current loader type.
     */
    private EntityLoader.Loader loader = EntityLoader.Loader.DEFAULT_LOADER;

    /**
     * Create new session instance.
     *
     * @param loaders          target loaders
     * @param connectionSource target connection source
     * @param connection       target database connection
     * @param sessionManager   session manager which create this instance
     */
    SessionImpl(ConnectionSource<?> connectionSource,
                Map<EntityLoader.Loader, EntityLoader> loaders,
                DatabaseConnection<?> connection,
                SessionManager sessionManager) {
        this.connectionSource = connectionSource;
        this.loaders = loaders;
        this.sessionManager = sessionManager;
        this.connection = connection;
    }

    @Override
    public CreateOrUpdateStatus createOrUpdate(Object object) throws SQLException {
        return loaders.get(loader).createOrUpdate(this, object);
    }

    @Override
    public int create(Object object) throws SQLException {
        return loaders.get(loader).create(this, object);
    }

    @Override
    public int create(Object... objects) throws SQLException {
        return loaders.get(loader).create(this, objects);
    }

    @Override
    public boolean createTable(Class<?> entityClass, boolean ifNotExist) throws SQLException {
        return loaders.get(loader).createTable(this, entityClass, ifNotExist);
    }

    @Override
    public Map<Class<?>, Boolean> createTables(Class<?>[] entityClasses, boolean ifNotExist) throws SQLException {
        Map<Class<?>, Boolean> result = new HashMap<>();

        for (Class<?> tableClass : entityClasses) {
            result.put(tableClass, createTable(tableClass, ifNotExist));
        }

        return result;
    }

    @Override
    public <T> T queryForId(Class<T> tClass, Object id) throws SQLException {
        return (T) loaders.get(loader).queryForId(this, tClass, id);
    }

    @Override
    public <T> List<T> queryForAll(Class<T> tClass) throws SQLException {
        return (List<T>) loaders.get(loader).queryForAll(this, tClass);
    }

    @Override
    public int update(Object object) throws SQLException {
        return loaders.get(loader).update(this, object);
    }

    @Override
    public int delete(Object object) throws SQLException {
        return loaders.get(loader).delete(this, object);
    }

    @Override
    public int deleteById(Class<?> tClass, Object id) throws SQLException {
        return loaders.get(loader).deleteById(this, tClass, id);
    }

    @Override
    public boolean refresh(Object object) throws SQLException {
        return loaders.get(loader).refresh(this, object);
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
    public boolean dropTable(Class<?> entityClass, boolean ifExist) throws SQLException {
        return loaders.get(loader).dropTable(this, entityClass, ifExist);
    }

    @Override
    public int clearTable(Class<?> entityClass) throws SQLException {
        return loaders.get(loader).clearTable(this, entityClass);
    }

    @Override
    public int clearTables(Class<?>... entityClasses) throws SQLException {
        int deletedCount = 0;

        for (Class<?> entityClass : entityClasses) {
            deletedCount += clearTable(entityClass);
        }

        return deletedCount;
    }

    @Override
    public void createIndexes(Class<?> tClass) throws SQLException {
        loaders.get(loader).createIndexes(this, tClass);
    }

    @Override
    public void dropIndexes(Class<?> tClass) throws SQLException {
        loaders.get(loader).dropIndexes(this, tClass);
    }

    @Override
    public long countOff(Class<?> tClass) throws SQLException {
        return loaders.get(loader).countOff(this, tClass);
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
    public <T> List<T> list(SelectStatement<T> criteria) throws SQLException {
        return (List<T>) loaders.get(loader).list(this, criteria);
    }

    @Override
    public long queryForLong(SelectStatement<?> selectStatement) throws SQLException {
        return loaders.get(loader).queryForLong(this, selectStatement);
    }

    @Override
    public DatabaseResults query(String query) throws SQLException {
        return loaders.get(loader).query(this, query);
    }

    @Override
    public <T> T uniqueResult(SelectStatement<T> selectStatement) throws SQLException {
        List<T> results = list(selectStatement);

        if (results.isEmpty()) {
            return null;
        }

        return results.iterator().next();
    }

    @Override
    public boolean exist(Class<?> entityClass, Object id) throws SQLException {
        return loaders.get(loader).exist(this, entityClass, id);
    }

    @Override
    public int delete(DeleteStatement deleteStatement) throws SQLException {
        return loaders.get(loader).delete(this, deleteStatement);
    }

    @Override
    public int update(UpdateStatement updateStatement) throws SQLException {
        return loaders.get(loader).update(this, updateStatement);
    }

    @Override
    public DatabaseResults query(SelectStatement<?> selectStatement) throws SQLException {
        return loaders.get(loader).query(this, selectStatement);
    }

    @Override
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    @Override
    public <T> DatabaseConnection<T> getConnection() {
        return (DatabaseConnection<T>) connection;
    }

    @Override
    public void close() throws SQLException {
        connectionSource.releaseConnection((DatabaseConnection) connection);
    }
}
