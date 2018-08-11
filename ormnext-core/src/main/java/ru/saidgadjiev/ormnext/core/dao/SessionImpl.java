package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.connection.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.connection.source.ConnectionSource;
import ru.saidgadjiev.ormnext.core.dao.transaction.state.BeginState;
import ru.saidgadjiev.ormnext.core.dao.transaction.state.SessionTransactionContract;
import ru.saidgadjiev.ormnext.core.dao.transaction.state.TransactionState;
import ru.saidgadjiev.ormnext.core.loader.EntityLoader;
import ru.saidgadjiev.ormnext.core.query.criteria.StatementBuilder;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.DeleteStatement;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.Query;
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
public class SessionImpl implements Session, SessionTransactionContract, SessionCriteriaContract {

    /**
     * Connection source.
     *
     * @see ConnectionSource
     */
    private final ConnectionSource<?> connectionSource;

    /**
     * Entity loader.
     */
    private EntityLoader entityLoader;

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
     * @param entityLoader     target loader
     * @param connectionSource target connection source
     * @param connection       target database connection
     * @param sessionManager   session manager which create this instance
     */
    SessionImpl(ConnectionSource<?> connectionSource,
                EntityLoader entityLoader,
                DatabaseConnection<?> connection,
                SessionManager sessionManager) {
        this.connectionSource = connectionSource;
        this.entityLoader = entityLoader;
        this.sessionManager = sessionManager;
        this.connection = connection;
    }

    @Override
    public CreateOrUpdateStatus createOrUpdate(Object object) throws SQLException {
        return entityLoader.createOrUpdate(this, object);
    }

    @Override
    public int create(Object object) throws SQLException {
        return entityLoader.create(this, object);
    }

    @Override
    public int create(Object... objects) throws SQLException {
        return entityLoader.create(this, objects);
    }

    @Override
    public boolean createTable(Class<?> entityClass, boolean ifNotExist) throws SQLException {
        return entityLoader.createTable(this, entityClass, ifNotExist);
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
        return (T) entityLoader.queryForId(this, tClass, id);
    }

    @Override
    public <T> List<T> queryForAll(Class<T> tClass) throws SQLException {
        return (List<T>) entityLoader.queryForAll(this, tClass);
    }

    @Override
    public int update(Object object) throws SQLException {
        return entityLoader.update(this, object);
    }

    @Override
    public int delete(Object object) throws SQLException {
        return entityLoader.delete(this, object);
    }

    @Override
    public int deleteById(Class<?> tClass, Object id) throws SQLException {
        return entityLoader.deleteById(this, tClass, id);
    }

    @Override
    public boolean refresh(Object object) throws SQLException {
        return entityLoader.refresh(this, object);
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
        return entityLoader.dropTable(this, entityClass, ifExist);
    }

    @Override
    public int clearTable(Class<?> entityClass) throws SQLException {
        return entityLoader.clearTable(this, entityClass);
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
        entityLoader.createIndexes(this, tClass);
    }

    @Override
    public void dropIndexes(Class<?> tClass) throws SQLException {
        entityLoader.dropIndexes(this, tClass);
    }

    @Override
    public long countOff(Class<?> tClass) throws SQLException {
        return entityLoader.countOff(this, tClass);
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
        return (List<T>) entityLoader.list(this, criteria);
    }

    @Override
    public long queryForLong(SelectStatement<?> selectStatement) throws SQLException {
        return entityLoader.queryForLong(this, selectStatement);
    }

    @Override
    public DatabaseResults executeQuery(Query query) throws SQLException {
        return entityLoader.executeQuery(this, query);
    }

    @Override
    public int executeUpdate(Query query) throws SQLException {
        return entityLoader.executeUpdate(this, query);
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
        return entityLoader.exist(this, entityClass, id);
    }

    @Override
    public int delete(DeleteStatement deleteStatement) throws SQLException {
        return entityLoader.delete(this, deleteStatement);
    }

    @Override
    public int update(UpdateStatement updateStatement) throws SQLException {
        return entityLoader.update(this, updateStatement);
    }

    @Override
    public DatabaseResults query(SelectStatement<?> selectStatement) throws SQLException {
        return entityLoader.query(this, selectStatement);
    }

    @Override
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return connection.isClosed() || sessionManager.isClosed();
    }

    @Override
    public <T> DatabaseConnection<T> getConnection() {
        return (DatabaseConnection<T>) connection;
    }

    @Override
    public StatementBuilder statementBuilder() {
        return new StatementBuilder(this);
    }

    @Override
    public void close() throws SQLException {
        connectionSource.releaseConnection((DatabaseConnection) connection);
    }
}
