package ru.saidgadjiev.orm.next.core.stament_executor;

import ru.saidgadjiev.orm.next.core.cache.CacheContext;
import ru.saidgadjiev.orm.next.core.cache.ObjectCache;
import ru.saidgadjiev.orm.next.core.criteria.impl.SelectStatement;
import ru.saidgadjiev.orm.next.core.field.field_type.DatabaseColumnType;
import ru.saidgadjiev.orm.next.core.field.field_type.ForeignCollectionFieldType;
import ru.saidgadjiev.orm.next.core.field.field_type.ForeignColumnype;
import ru.saidgadjiev.orm.next.core.field.field_type.IDatabaseColumnType;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.table.DatabaseEntityMetadata;
import ru.saidgadjiev.orm.next.core.table.TableInfoManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("CPD-START")
public class CachedStatementExecutor implements IStatementExecutor {

    private IStatementExecutor delegate;

    private CacheContext cacheContext;

    public CachedStatementExecutor(CacheContext cacheContext,
                                   IStatementExecutor delegate) {
        this.cacheContext = cacheContext;
        this.delegate = delegate;
    }

    @Override
    public<T> int create(Connection connection, Collection<T> objects) throws SQLException {
        if (objects.isEmpty()) {
            return 0;
        }
        Class<T> tClass = (Class<T>) objects.iterator().next().getClass();
        DatabaseEntityMetadata<T> databaseEntityMetadata = TableInfoManager.buildOrGet(tClass);

        delegate.create(connection, objects);

        if (cacheContext.isCaching(databaseEntityMetadata.getTableClass()) && cacheContext.getObjectCache().isPresent()) {
            ObjectCache objectCache = cacheContext.getObjectCache().get();

            if (databaseEntityMetadata.getPrimaryKey().isPresent()) {
                for (T object : objects) {
                    IDatabaseColumnType IDatabaseColumnType = databaseEntityMetadata.getPrimaryKey().get();

                    try {
                        objectCache.put((Class<T>) databaseEntityMetadata.getTableClass(), IDatabaseColumnType.access(object), object);
                    } catch (Exception ex) {
                        throw new SQLException(ex);
                    }
                }
            }
        }

        return 0;
    }

    @Override
    public<T> int create(Connection connection, T object) throws SQLException {
        DatabaseEntityMetadata<T> databaseEntityMetadata = TableInfoManager.buildOrGet((Class<T>) object.getClass());
        Integer count = delegate.create(connection, object);

        if (count > 0 && cacheContext.isCaching(databaseEntityMetadata.getTableClass()) && cacheContext.getObjectCache().isPresent()) {
            ObjectCache objectCache = cacheContext.getObjectCache().get();

            if (databaseEntityMetadata.getPrimaryKey().isPresent()) {
                IDatabaseColumnType IDatabaseColumnType = databaseEntityMetadata.getPrimaryKey().get();

                try {
                    objectCache.put((Class<T>) databaseEntityMetadata.getTableClass(), IDatabaseColumnType.access(object), object);
                } catch (Exception ex) {
                    throw new SQLException(ex);
                }
            }
        }

       try {
            for (IDatabaseColumnType fieldType: databaseEntityMetadata.getFieldTypes()) {
               if (fieldType.isForeignFieldType()) {
                   ForeignColumnype foreignColumnype = (ForeignColumnype) fieldType;
                   Class<?> foreignClass = foreignColumnype.getForeignFieldClass();
                   DatabaseEntityMetadata<?> foreignDatabaseEntityMetadata = TableInfoManager.buildOrGet(foreignClass);

                   for (ForeignCollectionFieldType foreignCollectionFieldType: foreignDatabaseEntityMetadata.toForeignCollectionFieldTypes()) {
                       if (foreignCollectionFieldType.getForeignFieldClass().equals(object.getClass())) {
                           Object foreignObject = foreignColumnype.access(object);

                           if (foreignObject != null) {
                               foreignCollectionFieldType.add(foreignObject, object);
                           }
                       }
                   }
               }
           }
       } catch (Exception ex) {
            throw new SQLException(ex);
       }

        return count;
    }

    @Override
    public<T> boolean createTable(Connection connection, Class<T> tClass, boolean ifNotExists) throws SQLException {
        return delegate.createTable(connection, tClass, ifNotExists);
    }

    @Override
    public<T> boolean dropTable(Connection connection, Class<T> tClass, boolean ifExists) throws SQLException {
        return delegate.dropTable(connection, tClass, ifExists);
    }

    @Override
    public<T> int update(Connection connection, T object) throws SQLException {
        DatabaseEntityMetadata<T> databaseEntityMetadata = TableInfoManager.buildOrGet((Class<T>) object.getClass());
        Integer count = delegate.update(connection, object);

        if (count > 0) {
            IDatabaseColumnType idFieldType = databaseEntityMetadata.getPrimaryKey().get();

            try {
                if (cacheContext.isCaching(databaseEntityMetadata.getTableClass()) && cacheContext.getObjectCache().isPresent()) {
                    ObjectCache objectCache = cacheContext.getObjectCache().get();
                    T cachedData = objectCache.get(databaseEntityMetadata.getTableClass(), idFieldType.access(object));

                    if (cachedData != null) {
                        copy(databaseEntityMetadata, object, cachedData);
                    }
                }
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }

        return count;
    }

    @Override
    public<T> int delete(Connection connection, T object) throws SQLException {
        DatabaseEntityMetadata<T> databaseEntityMetadata = TableInfoManager.buildOrGet((Class<T>) object.getClass());
        IDatabaseColumnType dbFieldType = databaseEntityMetadata.getPrimaryKey().get();
        Integer result = delegate.delete(connection, object);

        try {
            Object id = dbFieldType.access(object);

            if (cacheContext.isCaching(databaseEntityMetadata.getTableClass())) {
                cacheContext.getObjectCache().ifPresent(objectCache -> objectCache.invalidate(databaseEntityMetadata.getTableClass(), id));
            }
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }

        return result;
    }

    @Override
    public<T, ID> int deleteById(Connection connection, Class<T> tClass, ID id) throws SQLException {
        DatabaseEntityMetadata<T> databaseEntityMetadata = TableInfoManager.buildOrGet(tClass);
        Integer result = delegate.deleteById(connection, tClass, id);

        if (cacheContext.isCaching(databaseEntityMetadata.getTableClass())) {
            cacheContext.getObjectCache().ifPresent(objectCache -> objectCache.invalidate(databaseEntityMetadata.getTableClass(), id));
        }

        return result;
    }

    @Override
    public<T, ID> T queryForId(Connection connection, Class<T> tClass, ID id) throws SQLException {
        DatabaseEntityMetadata<T> databaseEntityMetadata = TableInfoManager.buildOrGet(tClass);

        if (cacheContext.isCaching(databaseEntityMetadata.getTableClass()) && cacheContext.getObjectCache().isPresent()) {
            ObjectCache objectCache = cacheContext.getObjectCache().get();

            if (objectCache.contains(databaseEntityMetadata.getTableClass(), id)) {
                return objectCache.get(databaseEntityMetadata.getTableClass(), id);
            }
        }

        T object = delegate.queryForId(connection, tClass, id);

        if (object != null && cacheContext.isCaching(databaseEntityMetadata.getTableClass())) {
            cacheContext.getObjectCache().ifPresent(objectCache -> objectCache.put((Class<T>) databaseEntityMetadata.getTableClass(), id, object));
        }

        return object;
    }

    @Override
    public<T> List<T> queryForAll(Connection connection, Class<T> tClass) throws SQLException {
        DatabaseEntityMetadata<T> databaseEntityMetadata = TableInfoManager.buildOrGet(tClass);
        List<T> result = delegate.queryForAll(connection, tClass);

        try {
            if (databaseEntityMetadata.getPrimaryKey().isPresent() && cacheContext.isCaching(databaseEntityMetadata.getTableClass()) && cacheContext.getObjectCache().isPresent()) {
                IDatabaseColumnType IDatabaseColumnType = databaseEntityMetadata.getPrimaryKey().get();
                ObjectCache objectCache = cacheContext.getObjectCache().get();

                for (T object : result) {
                    objectCache.put((Class<T>) databaseEntityMetadata.getTableClass(), IDatabaseColumnType.access(object), object);
                }
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }

        return result;
    }

    @Override
    public<T> void createIndexes(Connection connection, Class<T> tClass) throws SQLException {
        delegate.createIndexes(connection, tClass);
    }

    @Override
    public<T> void dropIndexes(Connection connection, Class<T> tClass) throws SQLException {
        delegate.dropIndexes(connection, tClass);
    }

    @Override
    public<T> long countOff(Connection connection, Class<T> tClass) throws SQLException {
        return delegate.countOff(connection, tClass);
    }

    @Override
    public<R> GenericResults<R> query(ConnectionSource connectionSource, SelectStatement<R> statement) throws SQLException {
        return delegate.query(connectionSource, statement);
    }

    private<T> void copy(DatabaseEntityMetadata<T> databaseEntityMetadata, T srcObject, T destObject) throws Exception {
        for (DatabaseColumnType fieldType : databaseEntityMetadata.toDBFieldTypes()) {
            fieldType.assign(destObject, fieldType.access(srcObject));
        }
    }
}
