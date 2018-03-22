package ru.saidgadjiev.orm.next.core.stament_executor;

import ru.saidgadjiev.orm.next.core.cache.CacheContext;
import ru.saidgadjiev.orm.next.core.cache.ObjectCache;
import ru.saidgadjiev.orm.next.core.criteria.impl.SelectStatement;
import ru.saidgadjiev.orm.next.core.field.field_type.DBFieldType;
import ru.saidgadjiev.orm.next.core.field.field_type.ForeignCollectionFieldType;
import ru.saidgadjiev.orm.next.core.field.field_type.ForeignFieldType;
import ru.saidgadjiev.orm.next.core.field.field_type.IDBFieldType;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.table.TableInfo;
import ru.saidgadjiev.orm.next.core.table.TableInfoManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuppressWarnings("CPD-START")
public class CachedStatementExecutor implements IStatementExecutor {

    private TableInfo<?> tableInfo;

    private IStatementExecutor delegate;

    private CacheContext cacheContext;

    public CachedStatementExecutor(TableInfo<?> tableInfo,
                                   CacheContext cacheContext,
                                   IStatementExecutor delegate) {
        this.tableInfo = tableInfo;
        this.cacheContext = cacheContext;
        this.delegate = delegate;
    }

    @Override
    public<T> int create(Connection connection, Collection<T> objects) throws SQLException {
        delegate.create(connection, objects);

        if (cacheContext.isCaching(tableInfo.getTableClass()) && cacheContext.getObjectCache().isPresent()) {
            ObjectCache objectCache = cacheContext.getObjectCache().get();

            if (tableInfo.getPrimaryKey().isPresent()) {
                for (T object : objects) {
                    IDBFieldType idbFieldType = tableInfo.getPrimaryKey().get();

                    try {
                        objectCache.put((Class<T>) tableInfo.getTableClass(), idbFieldType.access(object), object);
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
        Integer count = delegate.create(connection, object);

        if (count > 0 && cacheContext.isCaching(tableInfo.getTableClass()) && cacheContext.getObjectCache().isPresent()) {
            ObjectCache objectCache = cacheContext.getObjectCache().get();

            if (tableInfo.getPrimaryKey().isPresent()) {
                IDBFieldType idbFieldType = tableInfo.getPrimaryKey().get();

                try {
                    objectCache.put((Class<T>) tableInfo.getTableClass(), idbFieldType.access(object), object);
                } catch (Exception ex) {
                    throw new SQLException(ex);
                }
            }
        }

       try {
            for (IDBFieldType fieldType: tableInfo.getFieldTypes()) {
               if (fieldType.isForeignFieldType()) {
                   ForeignFieldType foreignFieldType = (ForeignFieldType) fieldType;
                   Class<?> foreignClass = foreignFieldType.getForeignFieldClass();
                   TableInfo<?> foreignTableInfo = TableInfoManager.buildOrGet(foreignClass);

                   for (ForeignCollectionFieldType foreignCollectionFieldType: foreignTableInfo.toForeignCollectionFieldTypes()) {
                       if (foreignCollectionFieldType.getForeignFieldClass().equals(object.getClass())) {
                           Object foreignObject = foreignFieldType.access(object);

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
    public boolean createTable(Connection connection, boolean ifNotExists) throws SQLException {
        return delegate.createTable(connection, ifNotExists);
    }

    @Override
    public boolean dropTable(Connection connection, boolean ifExists) throws SQLException {
        return delegate.dropTable(connection, ifExists);
    }

    @Override
    public<T> int update(Connection connection, T object) throws SQLException {
        Integer count = delegate.update(connection, object);

        if (count > 0) {
            IDBFieldType idFieldType = tableInfo.getPrimaryKey().get();

            try {
                if (cacheContext.isCaching(tableInfo.getTableClass()) && cacheContext.getObjectCache().isPresent()) {
                    ObjectCache objectCache = cacheContext.getObjectCache().get();
                    T cachedData = objectCache.get((Class<T>) tableInfo.getTableClass(), idFieldType.access(object));

                    if (cachedData != null) {
                        copy(object, cachedData);
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
        IDBFieldType dbFieldType = tableInfo.getPrimaryKey().get();
        Integer result = delegate.delete(connection, object);

        try {
            Object id = dbFieldType.access(object);

            if (cacheContext.isCaching(tableInfo.getTableClass())) {
                cacheContext.getObjectCache().ifPresent(objectCache -> objectCache.invalidate(tableInfo.getTableClass(), id));
            }
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }

        return result;
    }

    @Override
    public<ID> int deleteById(Connection connection, ID id) throws SQLException {
        Integer result = delegate.deleteById(connection, id);

        if (cacheContext.isCaching(tableInfo.getTableClass())) {
            cacheContext.getObjectCache().ifPresent(objectCache -> objectCache.invalidate(tableInfo.getTableClass(), id));
        }

        return result;
    }

    @Override
    public<T, ID> T queryForId(Connection connection, ID id) throws SQLException {
        if (cacheContext.isCaching(tableInfo.getTableClass()) && cacheContext.getObjectCache().isPresent()) {
            ObjectCache objectCache = cacheContext.getObjectCache().get();

            if (objectCache.contains(tableInfo.getTableClass(), id)) {
                return objectCache.get((Class<T>) tableInfo.getTableClass(), id);
            }
        }

        T object = delegate.queryForId(connection, id);

        if (object != null && cacheContext.isCaching(tableInfo.getTableClass())) {
            cacheContext.getObjectCache().ifPresent(objectCache -> objectCache.put((Class<T>) tableInfo.getTableClass(), id, object));
        }

        return object;
    }

    @Override
    public<T> List<T> queryForAll(Connection connection) throws SQLException {

        List<T> result = delegate.queryForAll(connection);
        try {
            if (tableInfo.getPrimaryKey().isPresent() && cacheContext.isCaching(tableInfo.getTableClass()) && cacheContext.getObjectCache().isPresent()) {
                IDBFieldType idbFieldType = tableInfo.getPrimaryKey().get();
                ObjectCache objectCache = cacheContext.getObjectCache().get();

                for (T object : result) {
                    objectCache.put((Class<T>) tableInfo.getTableClass(), idbFieldType.access(object), object);
                }
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }

        return result;
    }

    @Override
    public void createIndexes(Connection connection) throws SQLException {
        delegate.createIndexes(connection);
    }

    @Override
    public void dropIndexes(Connection connection) throws SQLException {
        delegate.dropIndexes(connection);
    }

    @Override
    public <R> GenericResults<R> query(ConnectionSource connectionSource, String query, Map<Integer, Object> args) throws SQLException {
        return delegate.query(connectionSource, query, args);
    }

    @Override
    public long queryForLong(Connection connection, String query) throws SQLException {
        return delegate.queryForLong(connection, query);
    }

    @Override
    public long countOff(Connection connection) throws SQLException {
        return delegate.countOff(connection);
    }

    @Override
    public<R> GenericResults<R> query(ConnectionSource connectionSource, SelectStatement<R> statement) throws SQLException {
        return delegate.query(connectionSource, statement);
    }

    private<T> void copy(T srcObject, T destObject) throws Exception {
        for (DBFieldType fieldType : tableInfo.toDBFieldTypes()) {
            fieldType.assign(destObject, fieldType.access(srcObject));
        }
    }
}
