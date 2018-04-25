package ru.saidgadjiev.orm.next.core.dao;

import ru.saidgadjiev.orm.next.core.cache.CacheContext;
import ru.saidgadjiev.orm.next.core.cache.ObjectCache;
import ru.saidgadjiev.orm.next.core.criteria.impl.CriteriaQuery;
import ru.saidgadjiev.orm.next.core.criteria.impl.SimpleCriteriaQuery;
import ru.saidgadjiev.orm.next.core.stamentexecutor.CacheHelper;
import ru.saidgadjiev.orm.next.core.stamentexecutor.DefaultEntityLoader;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.support.DatabaseConnection;
import ru.saidgadjiev.orm.next.core.table.internal.metamodel.MetaModel;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Created by said on 19.02.2018.
 */
public class DaoImpl implements Dao {

    private DefaultEntityLoader statementExecutor;

    private CacheHelper cacheHelper;

    private final ConnectionSource dataSource;

    private CacheContext cacheContext = new CacheContext();

    private MetaModel metaModel;

    private DatabaseEngine databaseEngine;

    DaoImpl(ConnectionSource dataSource, Collection<Class<?>> entityClasses, DatabaseEngine databaseEngine) {
        this.dataSource = dataSource;
        this.metaModel = new MetaModel(entityClasses);
        this.databaseEngine = databaseEngine;
        this.cacheHelper = new CacheHelper(cacheContext);

        this.statementExecutor = new DefaultEntityLoader(this);
    }

    @Override
    public <T> int create(Collection<T> objects) throws SQLException {
        DatabaseConnection connection = dataSource.getConnection();

        try {
            return statementExecutor.create(connection, objects);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public <T> int create(T object) throws SQLException {
        DatabaseConnection connection = dataSource.getConnection();

        try {
            return statementExecutor.create(connection, object);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public <T> boolean createTable(Class<T> tClass, boolean ifNotExist) throws SQLException {
        DatabaseConnection connection = dataSource.getConnection();

        try {
            return statementExecutor.createTable(connection, tClass, ifNotExist);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public <T, ID> T queryForId(Class<T> tClass, ID id) throws SQLException {
        DatabaseConnection connection = dataSource.getConnection();

        try {
            return statementExecutor.queryForId(connection, tClass, id);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public <T> List<T> queryForAll(Class<T> tClass) throws SQLException {
        DatabaseConnection connection = dataSource.getConnection();

        try {
            return statementExecutor.queryForAll(connection, tClass);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public <T> int update(T object) throws SQLException {
        DatabaseConnection connection = dataSource.getConnection();

        try {
            return statementExecutor.update(connection, object);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public <T> int delete(T object) throws SQLException {
        DatabaseConnection connection = dataSource.getConnection();

        try {
            return statementExecutor.delete(connection, object);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    public <T, ID> int deleteById(Class<T> tClass, ID id) throws SQLException {
        DatabaseConnection connection = dataSource.getConnection();

        try {
            return statementExecutor.deleteById(connection, tClass, id);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public <T> boolean dropTable(Class<T> tClass, boolean ifExists) throws SQLException {
        DatabaseConnection connection = dataSource.getConnection();

        try {
            return statementExecutor.dropTable(connection, tClass, ifExists);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public <T> void createIndexes(Class<T> tClass) throws SQLException {
        DatabaseConnection connection = dataSource.getConnection();

        try {
            statementExecutor.createIndexes(connection, tClass);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public <T> void dropIndexes(Class<T> tClass) throws SQLException {
        DatabaseConnection connection = dataSource.getConnection();

        try {
            statementExecutor.dropIndexes(connection, tClass);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public <T> long countOff(Class<T> tClass) throws SQLException {
        DatabaseConnection connection = dataSource.getConnection();

        try {
            return statementExecutor.countOff(connection, tClass);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public TransactionImpl transaction() throws SQLException {
        return new TransactionImpl(statementExecutor, dataSource);
    }

    @Override
    public CacheHelper cacheHelper() {
        return cacheHelper;
    }

    @Override
    public void close() throws SQLException {
        dataSource.close();
    }

    @Override
    public <T> List<T> list(CriteriaQuery<T> criteria) throws SQLException {
        DatabaseConnection connection = dataSource.getConnection();

        try {
            return statementExecutor.list(connection, criteria);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public long queryForLong(SimpleCriteriaQuery simpleCriteriaQuery) throws SQLException {
        DatabaseConnection connection = dataSource.getConnection();

        try {
            return statementExecutor.queryForLong(connection, simpleCriteriaQuery);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public void setObjectCache(ObjectCache objectCache, Class<?>... classes) {
        cacheContext.getObjectCache().invalidateAll();
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
    public ConnectionSource getDataSource() {
        return dataSource;
    }

    @Override
    public MetaModel getMetaModel() {
        return metaModel;
    }

    @Override
    public DatabaseEngine getDatabaseEngine() {
        return databaseEngine;
    }
}
