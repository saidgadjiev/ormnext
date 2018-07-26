package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.cache.Cache;
import ru.saidgadjiev.ormnext.core.connection.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.DeleteStatement;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.Query;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.UpdateStatement;

import java.sql.SQLException;
import java.util.*;

/**
 * Cache session.
 *
 * @author Said Gadjiev
 */
public class CacheSession implements Session {

    /**
     * Log.
     */
    private static final Log LOG = LoggerFactory.getLogger(CacheSession.class);

    /**
     * Cache.
     */
    private final Cache cache;

    /**
     * Decorated session.
     */
    private final Session session;

    /**
     * Create a new instance.
     *
     * @param cache   target cache
     * @param session target decorated session.
     */
    CacheSession(Cache cache, Session session) {
        this.cache = cache;
        this.session = session;
    }

    @Override
    public void beginTransaction() throws SQLException {
        session.beginTransaction();
    }

    @Override
    public void commit() throws SQLException {
        session.commit();
    }

    @Override
    public void rollback() throws SQLException {
        session.rollback();
    }

    @Override
    public SessionManager getSessionManager() {
        return session.getSessionManager();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return session.isClosed();
    }

    @Override
    public <T> DatabaseConnection<T> getConnection() {
        return session.getConnection();
    }

    @Override
    public void close() throws SQLException {
        session.close();
    }

    @Override
    public CreateOrUpdateStatus createOrUpdate(Object object) throws SQLException {
        CreateOrUpdateStatus createOrUpdateStatus = session.createOrUpdate(object);

        cache.create(object);

        return createOrUpdateStatus;
    }

    @Override
    public int create(Object object) throws SQLException {
        int result = session.create(object);

        cache.create(object);

        return result;
    }

    @Override
    public int create(Object... objects) throws SQLException {
        int result = session.create(objects);

        cache.create(Arrays.asList(objects));

        return result;
    }

    @Override
    public boolean createTable(Class<?> entityClass, boolean ifNotExist) throws SQLException {
        return session.createTable(entityClass, ifNotExist);
    }

    @Override
    public Map<Class<?>, Boolean> createTables(Class<?>[] entityClasses, boolean ifNotExist) throws SQLException {
        return session.createTables(entityClasses, ifNotExist);
    }

    @Override
    public boolean dropTable(Class<?> entityClass, boolean ifExist) throws SQLException {
        return session.dropTable(entityClass, ifExist);
    }

    @Override
    public Map<Class<?>, Boolean> dropTables(Class<?>[] entityClasses, boolean ifExist) throws SQLException {
        return session.dropTables(entityClasses, ifExist);
    }

    @Override
    public int clearTable(Class<?> entityClass) throws SQLException {
        return session.clearTable(entityClass);
    }

    @Override
    public int clearTables(Class<?>... entityClasses) throws SQLException {
        return session.clearTables(entityClasses);
    }

    @Override
    public <T> T queryForId(Class<T> tClass, Object id) throws SQLException {
        Optional<Object> resultOptional = cache.queryForId(tClass, id);

        if (resultOptional.isPresent()) {
            LOG.debug("Cache:queryForLong(%s)", resultOptional.get());

            return (T) resultOptional.get();
        }

        T result = session.queryForId(tClass, id);

        cache.cacheQueryForId(id, result);

        return result;
    }

    @Override
    public <T> List<T> queryForAll(Class<T> tClass) throws SQLException {
        Optional<List<Object>> resultsOptional = cache.queryForAll(tClass);

        if (resultsOptional.isPresent()) {
            LOG.debug("Cache:queryForAll(%s)", resultsOptional.get());

            return (List<T>) resultsOptional.get();
        }

        List<T> results = session.queryForAll(tClass);

        cache.cacheQueryForAll((Collection<Object>) results);

        return results;
    }

    @Override
    public int update(Object object) throws SQLException {
        int result = session.update(object);

        cache.update(object);

        return result;
    }

    @Override
    public int delete(Object object) throws SQLException {
        int result = session.delete(object);

        cache.delete(object);

        return result;
    }

    @Override
    public int deleteById(Class<?> tClass, Object id) throws SQLException {
        int result = session.deleteById(tClass, id);

        cache.deleteById(tClass, id);

        return result;
    }

    @Override
    public boolean refresh(Object object) throws SQLException {
        boolean result = session.refresh(object);

        cache.refresh(object);

        return result;
    }

    @Override
    public void createIndexes(Class<?> tClass) throws SQLException {
        session.createIndexes(tClass);
    }

    @Override
    public void dropIndexes(Class<?> tClass) throws SQLException {
        session.dropIndexes(tClass);
    }

    @Override
    public long countOff(Class<?> tClass) throws SQLException {
        Optional<Long> countOffOptional = cache.countOff(tClass);

        if (countOffOptional.isPresent()) {
            LOG.debug("Cache:countOff(%s)", countOffOptional.get());

            return countOffOptional.get();
        }

        long countOff = session.countOff(tClass);

        cache.cacheCountOff(tClass, countOff);

        return countOff;
    }

    @Override
    public <T> List<T> list(SelectStatement<T> selectStatement) throws SQLException {
        Optional<List<Object>> objects = cache.list(selectStatement);

        if (objects.isPresent()) {
            LOG.debug("Cache:list(%s)", objects.get());

            return (List<T>) objects.get();
        }

        List<T> results = session.list(selectStatement);

        cache.cacheList(selectStatement, (List<Object>) results);

        return results;
    }

    @Override
    public long queryForLong(SelectStatement<?> selectStatement) throws SQLException {
        Optional<Long> optionalLong = cache.queryForLong(selectStatement);

        if (optionalLong.isPresent()) {
            LOG.debug("Cache:queryForLong(%s)", optionalLong.get());

            return optionalLong.get();
        }

        long result = session.queryForLong(selectStatement);

        cache.cacheQueryForLong(selectStatement, result);

        return result;
    }

    @Override
    public DatabaseResults query(Query query) throws SQLException {
        Optional<DatabaseResults> resultsOptional = cache.query(query);

        if (resultsOptional.isPresent()) {
            return resultsOptional.get();
        }

        DatabaseResults results = session.query(query);

        cache.cacheQuery(query, results);

        return results;
    }

    @Override
    public <T> T uniqueResult(SelectStatement<T> selectStatement) throws SQLException {
        Optional<Object> optionalResult = cache.uniqueResult(selectStatement);

        if (optionalResult.isPresent()) {
            LOG.debug("Cache:uniqueResult(%s)", optionalResult.get());

            return (T) optionalResult.get();
        }

        Object object = session.uniqueResult(selectStatement);

        cache.cacheUniqueResult(selectStatement, object);

        return (T) object;
    }

    @Override
    public boolean exist(Class<?> entityClass, Object id) throws SQLException {
        Optional<Boolean> existOptional = cache.exist(entityClass, id);

        if (existOptional.isPresent()) {
            LOG.debug("Cache:exist(%s %s)", entityClass, id);

            return existOptional.get();
        }

        boolean exist = session.exist(entityClass, id);

        cache.cacheExist(entityClass, id, exist);

        return exist;
    }

    @Override
    public int delete(DeleteStatement deleteStatement) throws SQLException {
        int result = session.delete(deleteStatement);

        cache.delete(deleteStatement);

        return result;
    }

    @Override
    public int update(UpdateStatement updateStatement) throws SQLException {
        int result = session.update(updateStatement);

        cache.update(updateStatement);

        return result;
    }

    @Override
    public DatabaseResults query(SelectStatement selectStatement) throws SQLException {
        return session.query(selectStatement);
    }
}
