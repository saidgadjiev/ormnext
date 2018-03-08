package ru.saidgadjiev.orm.next.core.stament_executor;

import ru.saidgadjiev.orm.next.core.cache.CacheContext;
import ru.saidgadjiev.orm.next.core.cache.ObjectCache;
import ru.saidgadjiev.orm.next.core.criteria.impl.SelectStatement;
import ru.saidgadjiev.orm.next.core.field.field_type.DBFieldType;
import ru.saidgadjiev.orm.next.core.field.field_type.ForeignCollectionFieldType;
import ru.saidgadjiev.orm.next.core.field.field_type.ForeignFieldType;
import ru.saidgadjiev.orm.next.core.field.field_type.IDBFieldType;
import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.ResultsMapper;
import ru.saidgadjiev.orm.next.core.table.TableInfo;
import ru.saidgadjiev.orm.next.core.table.TableInfoManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("CPD-START")
public class CachedStatementExecutor<T, ID> implements IStatementExecutor<T, ID> {

    private TableInfo<T> tableInfo;

    private IStatementExecutor<T, ID> delegate;

    private CacheContext cacheContext;

    public CachedStatementExecutor(TableInfo<T> tableInfo,
                                   CacheContext cacheContext,
                                   IStatementExecutor<T, ID> delegate) {
        this.tableInfo = tableInfo;
        this.cacheContext = cacheContext;
        this.delegate = delegate;
    }

    @Override
    public int create(Connection connection, Collection<T> objects) throws SQLException {
        delegate.create(connection, objects);

        if (cacheContext.isCaching(tableInfo.getTableClass()) && cacheContext.getObjectCache().isPresent()) {
            ObjectCache objectCache = cacheContext.getObjectCache().get();

            if (tableInfo.getPrimaryKey().isPresent()) {
                for (T object : objects) {
                    IDBFieldType idbFieldType = tableInfo.getPrimaryKey().get();

                    try {
                        objectCache.put(tableInfo.getTableClass(), idbFieldType.access(object), object);
                    } catch (Exception ex) {
                        throw new SQLException(ex);
                    }
                }
            }
        }

        return 0;
    }

    @Override
    public int create(Connection connection, T object) throws SQLException {
        Integer count = delegate.create(connection, object);

        if (count > 0 && cacheContext.isCaching(tableInfo.getTableClass()) && cacheContext.getObjectCache().isPresent()) {
            ObjectCache objectCache = cacheContext.getObjectCache().get();

            if (tableInfo.getPrimaryKey().isPresent()) {
                IDBFieldType idbFieldType = tableInfo.getPrimaryKey().get();

                try {
                    objectCache.put(tableInfo.getTableClass(), idbFieldType.access(object), object);
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
    public int update(Connection connection, T object) throws SQLException {
        Integer count = delegate.update(connection, object);

        if (count > 0) {
            IDBFieldType idFieldType = tableInfo.getPrimaryKey().get();

            try {
                if (cacheContext.isCaching(tableInfo.getTableClass()) && cacheContext.getObjectCache().isPresent()) {
                    ObjectCache objectCache = cacheContext.getObjectCache().get();
                    T cachedData = objectCache.get(tableInfo.getTableClass(), idFieldType.access(object));

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
    public int delete(Connection connection, T object) throws SQLException {
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
    public int deleteById(Connection connection, ID id) throws SQLException {
        Integer result = delegate.deleteById(connection, id);

        if (cacheContext.isCaching(tableInfo.getTableClass())) {
            cacheContext.getObjectCache().ifPresent(objectCache -> objectCache.invalidate(tableInfo.getTableClass(), id));
        }

        return result;
    }

    @Override
    public T queryForId(Connection connection, ID id) throws SQLException {
        if (cacheContext.isCaching(tableInfo.getTableClass()) && cacheContext.getObjectCache().isPresent()) {
            ObjectCache objectCache = cacheContext.getObjectCache().get();

            if (objectCache.contains(tableInfo.getTableClass(), id)) {
                return objectCache.get(tableInfo.getTableClass(), id);
            }
        }

        T object = delegate.queryForId(connection, id);

        if (object != null && cacheContext.isCaching(tableInfo.getTableClass())) {
            cacheContext.getObjectCache().ifPresent(objectCache -> objectCache.put(tableInfo.getTableClass(), id, object));
        }

        return object;
    }

    @Override
    public List<T> queryForAll(Connection connection) throws SQLException {

        List<T> result = delegate.queryForAll(connection);
        try {
            if (tableInfo.getPrimaryKey().isPresent() && cacheContext.isCaching(tableInfo.getTableClass()) && cacheContext.getObjectCache().isPresent()) {
                IDBFieldType idbFieldType = tableInfo.getPrimaryKey().get();
                ObjectCache objectCache = cacheContext.getObjectCache().get();

                for (T object : result) {
                    objectCache.put(tableInfo.getTableClass(), idbFieldType.access(object), object);
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
    public <R> GenericResults<R> query(Connection connection, String query, ResultsMapper<R> resultsMapper) throws SQLException {
        return delegate.query(connection, query, resultsMapper);
    }

    @Override
    public long query(String query, Connection connection) throws SQLException {
        return delegate.query(query, connection);
    }

    @Override
    public long countOff(Connection connection) throws SQLException {
        return delegate.countOff(connection);
    }

    @Override
    public List<T> query(Connection connection, SelectStatement<T> statement) throws SQLException {
        return delegate.query(connection, statement);
    }

    private void copy(T srcObject, T destObject) throws Exception {
        for (DBFieldType fieldType : tableInfo.toDBFieldTypes()) {
            fieldType.assign(destObject, fieldType.access(srcObject));
        }
    }
}
