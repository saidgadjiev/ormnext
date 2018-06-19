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
     * @param connectionSource target connection source
     * @param connection       target database connection
     * @param sessionManager   session manager which create this instance
     */
    SessionImpl(ConnectionSource<?> connectionSource,
                DatabaseConnection<?> connection,
                SessionManager sessionManager) {
        this.connectionSource = connectionSource;
        this.sessionManager = sessionManager;
        this.connection = connection;
    }

    @Override
    public CreateOrUpdateStatus createOrUpdate(Object object) throws SQLException {
        return sessionManager.loader(loader).createOrUpdate(this, object);
    }

    @Override
    public int create(Object object) throws SQLException {
        return sessionManager.loader(loader).create(this, object);
    }

    @Override
    public int create(Object ... objects) throws SQLException {
        return sessionManager.loader(loader).create(this, objects);
    }

    @Override
    public boolean createTable(boolean ifNotExist, Class<?> entityClass) throws SQLException {
        return sessionManager.loader(loader).createTable(this, entityClass, ifNotExist);
    }

    @Override
    public Map<Class<?>, Boolean> createTables(boolean ifNotExist, Class<?> ... entityClasses) throws SQLException {
        Map<Class<?>, Boolean> result = new HashMap<>();

        for (Class<?> tableClass : entityClasses) {
            result.put(tableClass, createTable(ifNotExist, tableClass));
        }

        return result;
    }

    @Override
    public <T> T queryForId(Class<T> tClass, Object id) throws SQLException {
        return sessionManager.loader(loader).queryForId(this, tClass, id);
    }

    @Override
    public <T> List<T> queryForAll(Class<T> tClass) throws SQLException {
        return sessionManager.loader(loader).queryForAll(this, tClass);
    }

    @Override
    public int update(Object object) throws SQLException {
        return sessionManager.loader(loader).update(this, object);
    }

    @Override
    public int delete(Object object) throws SQLException {
        return sessionManager.loader(loader).delete(this, object);
    }

    @Override
    public int deleteById(Class<?> tClass, Object id) throws SQLException {
        return sessionManager.loader(loader).deleteById(this, tClass, id);
    }

    @Override
    public boolean refresh(Object object) throws SQLException {
        return sessionManager.loader(loader).refresh(this, object);
    }

    @Override
    public Map<Class<?>, Boolean> dropTables(boolean ifExist, Class<?> ... classes) throws SQLException {
        Map<Class<?>, Boolean> result = new HashMap<>();

        for (Class<?> tableClass : classes) {
            result.put(tableClass, dropTable(ifExist, tableClass));
        }

        return result;
    }

    @Override
    public boolean dropTable(boolean ifExist, Class<?> entityClass) throws SQLException {
        return sessionManager.loader(loader).dropTable(this, entityClass, ifExist);
    }

    @Override
    public int clearTable(Class<?> entityClass) throws SQLException {
        return sessionManager.loader(loader).clearTable(this, entityClass);
    }

    @Override
    public int clearTables(Class<?> ... entityClasses) throws SQLException {
        int deletedCount = 0;

        for (Class<?> entityClass : entityClasses) {
            deletedCount += clearTable(entityClass);
        }

        return deletedCount;
    }

    @Override
    public void createIndexes(Class<?> tClass) throws SQLException {
        sessionManager.loader(loader).createIndexes(this, tClass);
    }

    @Override
    public void dropIndexes(Class<?> tClass) throws SQLException {
        sessionManager.loader(loader).dropIndexes(this, tClass);
    }

    @Override
    public long countOff(Class<?> tClass) throws SQLException {
        return sessionManager.loader(loader).countOff(this, tClass);
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
        return sessionManager.loader(loader).list(this, criteria);
    }

    @Override
    public long queryForLong(SelectStatement<?> selectStatement) throws SQLException {
        return sessionManager.loader(loader).queryForLong(this, selectStatement);
    }

    @Override
    public DatabaseResults query(String query) throws SQLException {
        return sessionManager.loader(loader).query(this, query);
    }

    @Override
    public void batch() {
        loader = EntityLoader.Loader.BATCH_LOADER;

        sessionManager.loader(loader).batch();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        int[] batchResults = sessionManager.loader(loader).executeBatch(this);

        loader = EntityLoader.Loader.DEFAULT_LOADER;

        return batchResults;
    }

    @Override
    public <T> T uniqueResult(SelectStatement<T> selectStatement) throws SQLException {
        List<T> results = sessionManager.loader(loader).list(this, selectStatement);

        if (results.isEmpty()) {
            return null;
        }

        return results.iterator().next();
    }

    @Override
    public boolean exist(Class<?> entityClass, Object id) throws SQLException {
        return sessionManager.loader(loader).exist(this, entityClass, id);
    }

    @Override
    public int delete(DeleteStatement deleteStatement) throws SQLException {
        return sessionManager.loader(loader).delete(this, deleteStatement);
    }

    @Override
    public int update(UpdateStatement updateStatement) throws SQLException {
        return sessionManager.loader(loader).update(this, updateStatement);
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
