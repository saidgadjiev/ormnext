package ru.saidgadjiev.ormnext.core.loader;

import ru.saidgadjiev.ormnext.core.cache.Cache;
import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.dao.Dao;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.field.FetchType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.ormnext.core.loader.object.Lazy;
import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.DeleteStatement;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.Query;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.UpdateStatement;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Cache entityLoader.
 *
 * @author Said Gadjiev
 */
public class CacheEntityLoader implements EntityLoader {

    /**
     * Log.
     */
    private static final Log LOG = LoggerFactory.getLogger(CacheEntityLoader.class);

    /**
     * Cache.
     */
    private final Cache cache;

    /**
     * Meta model.
     */
    private MetaModel metaModel;

    /**
     * Decorated entityLoader.
     */
    private final EntityLoader entityLoader;

    /**
     * Create a new instance.
     *
     * @param cache        target cache
     * @param metaModel    meta model
     * @param entityLoader target decorated entityLoader.
     */
    public CacheEntityLoader(Cache cache, MetaModel metaModel, EntityLoader entityLoader) {
        this.cache = cache;
        this.metaModel = metaModel;
        this.entityLoader = entityLoader;
    }

    @Override
    public Dao.CreateOrUpdateStatus createOrUpdate(Session session, Object object) throws SQLException {
        Dao.CreateOrUpdateStatus createOrUpdateStatus = entityLoader.createOrUpdate(session, object);

        cache.create(object);

        return createOrUpdateStatus;
    }

    @Override
    public int create(Session session, Object object) throws SQLException {
        int result = entityLoader.create(session, object);

        cache.create(object);

        return result;
    }

    @Override
    public int create(Session session, Object... objects) throws SQLException {
        int result = entityLoader.create(session, objects);

        cache.create(Arrays.asList(objects));

        return result;
    }

    @Override
    public boolean createTable(Session session, Class<?> entityClass, boolean ifNotExist) throws SQLException {
        return entityLoader.createTable(session, entityClass, ifNotExist);
    }

    @Override
    public boolean dropTable(Session session, Class<?> entityClass, boolean ifExist) throws SQLException {
        return entityLoader.dropTable(session, entityClass, ifExist);
    }

    @Override
    public int clearTable(Session session, Class<?> entityClass) throws SQLException {
        return entityLoader.clearTable(session, entityClass);
    }

    @Override
    public Object queryForId(Session session, Class<?> tClass, Object id) throws SQLException {
        Optional<Object> resultOptional = cache.queryForId(tClass, id);

        if (resultOptional.isPresent()) {
            setLazyNonInitialized(resultOptional.get());

            LOG.debug("Cache:queryForId(%s)", resultOptional.get());

            return resultOptional.get();
        }

        Object result = entityLoader.queryForId(session, tClass, id);

        cache.cacheQueryForId(id, result);

        return result;
    }

    @Override
    public List<Object> queryForAll(Session session, Class<?> tClass) throws SQLException {
        Optional<List<Object>> resultsOptional = cache.queryForAll(tClass);

        if (resultsOptional.isPresent()) {
            setLazyNonInitialized(resultsOptional.get());
            LOG.debug("Cache:queryForAll(%s)", resultsOptional.get());

            return resultsOptional.get();
        }

        List<Object> results = entityLoader.queryForAll(session, tClass);

        cache.cacheQueryForAll(results);

        return results;
    }

    @Override
    public int update(Session session, Object object) throws SQLException {
        int result = entityLoader.update(session, object);

        cache.update(object);

        return result;
    }

    @Override
    public int delete(Session session, Object object) throws SQLException {
        int result = entityLoader.delete(session, object);

        cache.delete(object);

        return result;
    }

    @Override
    public int deleteById(Session session, Class<?> tClass, Object id) throws SQLException {
        int result = entityLoader.deleteById(session, tClass, id);

        cache.deleteById(tClass, id);

        return result;
    }

    @Override
    public boolean refresh(Session session, Object object) throws SQLException {
        boolean result = entityLoader.refresh(session, object);

        cache.refresh(object);

        return result;
    }

    @Override
    public void createIndexes(Session session, Class<?> tClass) throws SQLException {
        entityLoader.createIndexes(session, tClass);
    }

    @Override
    public void dropIndexes(Session session, Class<?> tClass) throws SQLException {
        entityLoader.dropIndexes(session, tClass);
    }

    @Override
    public long countOff(Session session, Class<?> tClass) throws SQLException {
        Optional<Long> countOffOptional = cache.countOff(tClass);

        if (countOffOptional.isPresent()) {
            LOG.debug("Cache:countOff(%s)", countOffOptional.get());

            return countOffOptional.get();
        }

        long countOff = entityLoader.countOff(session, tClass);

        cache.cacheCountOff(tClass, countOff);

        return countOff;
    }

    @Override
    public List<Object> list(Session session, SelectStatement<?> selectStatement) throws SQLException {
        Optional<List<Object>> objects = cache.list(selectStatement);

        if (objects.isPresent()) {
            setLazyNonInitialized(objects.get());
            LOG.debug("Cache:list(%s)", objects.get());

            return objects.get();
        }

        List<Object> results = entityLoader.list(session, selectStatement);

        cache.cacheList(selectStatement, results);

        return results;
    }

    @Override
    public long queryForLong(Session session, SelectStatement<?> selectStatement) throws SQLException {
        Optional<Long> optionalLong = cache.queryForLong(selectStatement);

        if (optionalLong.isPresent()) {
            LOG.debug("Cache:queryForLong(%s)", optionalLong.get());

            return optionalLong.get();
        }

        long result = entityLoader.queryForLong(session, selectStatement);

        cache.cacheQueryForLong(selectStatement, result);

        return result;
    }

    @Override
    public DatabaseResults query(Session session, Query query) throws SQLException {
        Optional<DatabaseResults> resultsOptional = cache.query(query);

        if (resultsOptional.isPresent()) {
            return resultsOptional.get();
        }

        DatabaseResults results = entityLoader.query(session, query);

        cache.cacheQuery(query, results);

        return results;
    }

    @Override
    public boolean exist(Session session, Class<?> entityClass, Object id) throws SQLException {
        Optional<Boolean> existOptional = cache.exist(entityClass, id);

        if (existOptional.isPresent()) {
            LOG.debug("Cache:exist(%s %s)", entityClass, id);

            return existOptional.get();
        }

        boolean exist = entityLoader.exist(session, entityClass, id);

        cache.cacheExist(entityClass, id, exist);

        return exist;
    }

    @Override
    public int delete(Session session, DeleteStatement deleteStatement) throws SQLException {
        int result = entityLoader.delete(session, deleteStatement);

        cache.delete(deleteStatement);

        return result;
    }

    @Override
    public int update(Session session, UpdateStatement updateStatement) throws SQLException {
        int result = entityLoader.update(session, updateStatement);

        cache.update(updateStatement);

        return result;
    }

    @Override
    public DatabaseResults query(Session session, SelectStatement selectStatement) throws SQLException {
        return entityLoader.query(session, selectStatement);
    }

    /**
     * Set lazy non initialized for object.
     *
     * @param data target object
     */
    private void setLazyNonInitialized(Object data) {
        DatabaseEntityMetadata<?> metadata = metaModel.getPersister(data.getClass()).getMetadata();

        for (ForeignColumnType columnType : metadata.toForeignColumnTypes()) {
            if (columnType.getFetchType().equals(FetchType.LAZY)) {
                Object lazy = columnType.access(data);

                ((Lazy) lazy).setNonInitialized();
            }
        }
        for (ForeignColumnType columnType : metadata.toForeignCollectionColumnTypes()) {
            if (columnType.getFetchType().equals(FetchType.LAZY)) {
                Object lazy = columnType.access(data);

                ((Lazy) lazy).setNonInitialized();
            }
        }
    }

    /**
     * Set lazy non initialized for objects.
     *
     * @param data target objects
     */
    private void setLazyNonInitialized(Collection<Object> data) {
        data.forEach(this::setLazyNonInitialized);
    }
}
